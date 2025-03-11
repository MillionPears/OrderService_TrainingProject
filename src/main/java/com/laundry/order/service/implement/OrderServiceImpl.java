package com.laundry.order.service.implement;

import com.laundry.order.dto.request.OrderCreateRequest;
import com.laundry.order.dto.request.OrderItemCreateRequest;
import com.laundry.order.dto.response.OrderItemResponse;
import com.laundry.order.dto.response.OrderResponse;
import com.laundry.order.entity.Order;
import com.laundry.order.entity.OrderItem;
import com.laundry.order.entity.User;
import com.laundry.order.enums.OrderStatus;
import com.laundry.order.event.InventoryEvent;
import com.laundry.order.event.PaymentEvent;
import com.laundry.order.exception.CustomException;
import com.laundry.order.exception.ErrorCode;
import com.laundry.order.kafka.producer.OrderKafkaProducer;
import com.laundry.order.mapstruct.OrderMapper;
import com.laundry.order.repository.OrderItemRepository;
import com.laundry.order.repository.OrderRepository;
import com.laundry.order.repository.UserRepository;

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

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final OrderMapper mapper;
  private final OrderItemRepository orderItemRepository;
  private final OrderKafkaProducer orderKafkaProducer;

//  @Override
//  public List<ProductResponse>getProductById(List<UUID> productIds) {
//    return productClient.getProductById(productIds);
//  }

  @Override
  @Transactional
  @Retryable(
    retryFor = OptimisticLockingFailureException.class,
    maxAttempts = 3,
    backoff = @Backoff(delay = 200, multiplier = 2)
  )
  public OrderResponse createOrder(OrderCreateRequest orderCreateRequest, String idempotentKey) {

    User user = findUserById(orderCreateRequest.getUserId());
    Order order = buildOrder(orderCreateRequest, user.getId());
    order.setIdempotentKey(UUID.fromString(idempotentKey));
    log.info("[ORDER CREATE] -- Saving order to database");
    orderRepository.save(order);
    List<OrderItem> orderItems = buildOrderItems(orderCreateRequest, order.getId());
    orderItemRepository.saveAll(orderItems);
    log.info("[ORDER CREATE] -- Order successfully created with orderId = {}", order.getId());
    OrderResponse orderResponse = buildOrderResponse(order, user, orderItems);
    publishOrderEvents(orderResponse);
    return orderResponse;
  }
  private User findUserById(UUID userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "User not found", userId));
  }

  private void publishOrderEvents(OrderResponse orderResponse) {
    InventoryEvent inventoryEvent = new InventoryEvent(orderResponse.getId(),
      orderResponse.getItems().stream()
        .map(item -> new InventoryEvent.ProductQuantity(item.getProductId(), item.getQuantity()))
        .toList()
    );
    orderKafkaProducer.sendInventoryEvent(inventoryEvent);
    log.info("[ORDER EVENT] -- Sent InventoryEvent: {}", inventoryEvent);

    PaymentEvent paymentEvent = PaymentEvent.builder()
      .orderId(orderResponse.getId())
      .totalAmount(orderResponse.getTotalAmount())
      .paymentMethod(orderResponse.getPaymentMethod().toString())
      .build();
    orderKafkaProducer.sendPaymentEvent(paymentEvent);
    log.info("[ORDER EVENT] -- Sent PaymentEvent: {}", paymentEvent);
  }

  private Order buildOrder(OrderCreateRequest request, UUID userId) {
    Order order = mapper.toEntity(request);
    order.setUserId(userId);
    order.setStatus(OrderStatus.PENDING);
    return order;
  }

  private List<OrderItem> buildOrderItems(OrderCreateRequest orderCreateRequest, Long orderId) {
    List<OrderItem> orderItems = new ArrayList<>();

    for (OrderItemCreateRequest itemRequest : orderCreateRequest.getItems()) {

      OrderItem orderItem = OrderItem.builder()
        .orderId(orderId)
        .productId(itemRequest.getProductId())
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
      .items(mapToOrderItemResponse( orderItems))
      .totalAmount(calculateTotalAmount(orderItems))
      .status(order.getStatus())
      .build();
  }

  private List<OrderItemResponse> mapToOrderItemResponse( List<OrderItem> items) {
    return items.stream()
      .map(item -> {
        return OrderItemResponse.builder()
          .productId(item.getProductId())
          .quantity(item.getQuantity())
          .price(item.getPrice())
          .build();
      }).toList();
  }

}