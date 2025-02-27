package com.laundry.order.service.implement;

import com.laundry.order.dto.request.OrderCreateRequest;
import com.laundry.order.dto.request.OrderItemCreateRequest;
import com.laundry.order.dto.response.OrderItemResponse;
import com.laundry.order.dto.response.OrderResponse;
import com.laundry.order.entity.Order;
import com.laundry.order.entity.OrderItem;
import com.laundry.order.entity.Product;
import com.laundry.order.entity.User;
import com.laundry.order.enums.OrderStatus;
import com.laundry.order.exception.CustomException;
import com.laundry.order.exception.ErrorCode;
import com.laundry.order.mapstruct.OrderMapper;
import com.laundry.order.repository.OrderRepository;
import com.laundry.order.repository.ProductRepository;
import com.laundry.order.repository.UserRepository;
import com.laundry.order.service.InventoryService;
import com.laundry.order.service.OrderService;
import com.laundry.order.service.PaymentService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final PaymentService paymentService;
  private final InventoryService inventoryService;
  private final OrderMapper mapper;
  private final OrderTransactionService orderTransactionService;


  @Override
  @Transactional
  @Retryable(
    retryFor = {
      ObjectOptimisticLockingFailureException.class,
//      OptimisticLockingFailureException.class,
//      OptimisticLockException.class,
//      CannotAcquireLockException.class
    },
    maxAttempts = 3,
    backoff = @Backoff(delay = 100, multiplier = 2, maxDelay = 1000)
  )
  public OrderResponse createOrder(OrderCreateRequest orderCreateRequest) {
    try {
      validateStock(orderCreateRequest);
    } catch (RuntimeException exception) {
      throw new CustomException(ErrorCode.CONFLICT);
    }
    if(orderCreateRequest.getIdempotentKey()!=null) {
      return retryCreateOrder(orderCreateRequest.getIdempotentKey());
    }
    Order order = buildOrder(orderCreateRequest);
    processOrderItems(order, orderCreateRequest.getItems());
    order = orderTransactionService.saveOrder(order);

    for (OrderItem item : order.getItems()) {
      try {
        inventoryService.reduceStock(item.getProduct().getId(), item.getQuantity());
        reduceProductStock(item.getProduct().getId(), item.getQuantity());
      } catch (OptimisticLockException e) {
        throw e;
      }
    }
    BigDecimal totalAmount = calculateTotalAmount(order);
    paymentService.create(order, totalAmount);
    return mapToOrderResponse(order, totalAmount);
  }

  private Product getProduct(UUID productId) {
    return productRepository.findById(productId)
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
  }



  private void validateStock(OrderCreateRequest orderCreateRequest) {
    for (OrderItemCreateRequest item : orderCreateRequest.getItems()) {
      Product product = getProduct(item.getProductId());
      if (product.getStockQuantity() < item.getQuantity()) {
        throw new RuntimeException("Not enough stock for product: " + product.getName());
      }
    }
  }
  private void reduceProductStock(UUID productId, int quantity){
    Product product = productRepository.findById(productId).orElseThrow(
      ()-> new CustomException(ErrorCode.NOT_FOUND));
    product.setStockQuantity(product.getStockQuantity() - quantity);
    productRepository.save(product);
  }
  private Order buildOrder(OrderCreateRequest request) {
    User user = userRepository.findById(request.getUserId())
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    Order order = mapper.toEntity(request);
    order.setUser(user);
    order.setStatus(OrderStatus.PENDING);
    if (request.getIdempotentKey() == null) {
      order.setIdempotentKey(UUID.randomUUID());
    }
    return order;
  }

  private void processOrderItems(Order order, List<OrderItemCreateRequest> items) {
    order.setItems(new ArrayList<>());
    for (OrderItemCreateRequest item : items) {
      Product product = getProduct(item.getProductId());
      OrderItem orderItem = OrderItem.builder()
        .order(order)
        .product(product)
        .quantity(item.getQuantity())
        .price(item.getPrice())
        .build();
      order.getItems().add(orderItem);
    }
  }
  private OrderResponse retryCreateOrder(UUID idempotentKey){
      Order order = orderRepository.findByIdempotentKey(idempotentKey).orElseThrow(
        ()-> new CustomException(ErrorCode.NOT_FOUND)
      );
      return mapToOrderResponse(order, calculateTotalAmount(order));

  }
  private BigDecimal calculateTotalAmount(Order order) {
    return order.getItems().stream()
      .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private OrderResponse mapToOrderResponse(Order order, BigDecimal totalAmount) {
    OrderResponse response = OrderResponse.builder()
      .id(order.getId())
      .customerName(order.getCustomerName())
      .phoneNumber(order.getPhoneNumber())
      .address(order.getAddress())
      .status(order.getStatus())
      .paymentMethod(order.getPaymentMethod())
      .totalAmount(totalAmount)
      .userId(order.getUser().getId())
      .createdDate(order.getCreatedDate())
      .build();

    List<OrderItemResponse> itemResponses = order.getItems().stream()
      .map(item -> {
        return OrderItemResponse.builder()
          .productName(item.getProduct().getName())
          .quantity(item.getQuantity())
          .price(item.getPrice())
          .build();
      }).collect(Collectors.toList());

    response.setItems(itemResponses);
    return response;
  }
}
