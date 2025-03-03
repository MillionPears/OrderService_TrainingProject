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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final InventoryService inventoryService;
  private final OrderMapper mapper;

  @Override
  @Transactional
  @Retryable(
    retryFor = OptimisticLockingFailureException.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 200, multiplier = 2)
  )
  public OrderResponse createOrder(OrderCreateRequest orderCreateRequest, String idempotentKey) {
    Optional<Order> existingOrder = orderRepository.findByIdempotentKey(UUID.fromString(idempotentKey));
    if (existingOrder.isPresent()) {
      log.info("[ORDER SERVICE] - Order already exists: [IdempotentKey = {}], return existing Order!", idempotentKey);
      return buildOrderResponse(existingOrder.get(), existingOrder.get().getUser(), existingOrder.get().getItems());
    }
    User user = findUserById(orderCreateRequest.getUserId());
    Map<UUID, Product> productMap = getProductMap(orderCreateRequest);
    Map<UUID, Integer> productQuantities = extractProductQuantities(orderCreateRequest);

    inventoryService.reduceStock(productQuantities);

    Order order = buildOrder(orderCreateRequest, user);
    List<OrderItem> orderItems = buildOrderItems(orderCreateRequest, order, productMap);

    order.setItems(orderItems);
    order.setIdempotentKey(UUID.fromString(idempotentKey));
    log.info("[ORDER CREATE] -- Saving order to database");
    orderRepository.save(order);

    log.info("[ORDER CREATE] -- Order successfully created with orderId = {}", order.getId());
    return buildOrderResponse(order, user, orderItems);
  }

  private User findUserById(UUID userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User not found", userId));
  }

  private Map<UUID, Product> getProductMap(OrderCreateRequest orderCreateRequest) {
    List<UUID> productIds = orderCreateRequest.getItems().stream()
      .map(OrderItemCreateRequest::getProductId)
      .collect(Collectors.toList());
    List<Product> products = productRepository.findAllById(productIds);
    log.debug("[ORDER SERVICE] -- Retrieved {} products", products.size());
    return products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
  }

  private Map<UUID, Integer> extractProductQuantities(OrderCreateRequest orderCreateRequest) {
    return orderCreateRequest.getItems().stream()
      .collect(Collectors.toMap(OrderItemCreateRequest::getProductId, OrderItemCreateRequest::getQuantity));
  }


  private Order buildOrder(OrderCreateRequest request, User user) {
    Order order = mapper.toEntity(request);
    order.setUser(user);
    order.setStatus(OrderStatus.PENDING);
    return order;
  }

  private List<OrderItem> buildOrderItems(OrderCreateRequest orderCreateRequest, Order order, Map<UUID, Product> productMap) {
    List<OrderItem> orderItems = new ArrayList<>();

    for (OrderItemCreateRequest itemRequest : orderCreateRequest.getItems()) {
      Product product = productMap.get(itemRequest.getProductId());

      OrderItem orderItem = OrderItem.builder()
        .order(order)
        .product(product)
        .quantity(itemRequest.getQuantity())
        .price(itemRequest.getPrice())
        .build();

      orderItems.add(orderItem);
    }
    return orderItems;
  }

  private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
    return orderItems.stream()
      .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private OrderResponse buildOrderResponse(Order order, User user, List<OrderItem> orderItems) {
    return OrderResponse.builder()
      .id(order.getId())
      .phoneNumber(order.getPhoneNumber())
      .customerName(order.getCustomerName())
      .address(order.getAddress())
      .paymentMethod(order.getPaymentMethod())
      .note(order.getNote())
      .userId(user.getId())
      .createdDate(order.getCreatedDate())
      .idempotentKey(order.getIdempotentKey())
      .items(mapToOrderItemResponse(order, orderItems))
      .totalAmount(calculateTotalAmount(orderItems))
      .status(order.getStatus())
      .build();
  }

  private List<OrderItemResponse> mapToOrderItemResponse(Order order, List<OrderItem> items) {
    return order.getItems().stream()
      .map(item -> {
        return OrderItemResponse.builder()
          .productName(item.getProduct().getName())
          .quantity(item.getQuantity())
          .price(item.getPrice())
          .build();
      }).toList();
  }

}