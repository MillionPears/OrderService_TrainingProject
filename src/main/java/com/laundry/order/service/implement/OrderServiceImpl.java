package com.laundry.order.service.implement;

import com.laundry.order.dto.request.OrderCreateRequest;
import com.laundry.order.dto.request.OrderItemCreateRequest;
import com.laundry.order.dto.response.OrderItemResponse;
import com.laundry.order.dto.response.OrderResponse;
import com.laundry.order.entity.Order;
import com.laundry.order.entity.OrderItem;
import com.laundry.order.entity.OutboxEvent;
import com.laundry.order.entity.User;
import com.laundry.order.enums.OrderStatus;
import com.laundry.order.enums.OutBoxEventStatus;
import com.laundry.order.event.PaymentEvent;
import com.laundry.order.exception.CustomException;
import com.laundry.order.exception.ErrorCode;
import com.laundry.order.mapstruct.OrderMapper;
import com.laundry.order.parser.Parser;
import com.laundry.order.repository.FeignClient.ProductFeignClient;
import com.laundry.order.repository.OrderItemRepository;
import com.laundry.order.repository.OrderRepository;
import com.laundry.order.repository.OutBoxRepository;
import com.laundry.order.repository.UserRepository;
import com.laundry.order.service.OrderService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final OrderMapper mapper;
  private final OrderItemRepository orderItemRepository;
  private final Parser parseToJson;
  private final OutBoxRepository outBoxRepository;
  private final ProductFeignClient productFeignClient;


  @Override
  @Transactional
  @Retryable(retryFor = {ObjectOptimisticLockingFailureException.class,}, maxAttempts = 3, backoff = @Backoff(delay = 100, multiplier = 2, maxDelay = 1000))
  public void processCreateOrder(OrderCreateRequest orderCreateRequest, Long orderId) {
    Map<UUID, Integer> productQuantities = orderCreateRequest.getItems().stream().collect(Collectors.toMap(OrderItemCreateRequest::getProductId, OrderItemCreateRequest::getQuantity));
    try {
      productFeignClient.checkInventoryAndReduceStock(productQuantities);
    } catch (FeignException.Conflict e) {
      throw new ObjectOptimisticLockingFailureException("Inventory service encountered optimistic lock", e);
    } catch (FeignException.NotFound f) {
      throw new CustomException(ErrorCode.PRODUCT_OUT_OF_STOCK,orderCreateRequest.getItems().getFirst().getProductId());
    }
    Order order = orderRepository.findById(orderId).get();
    order.setStatus(OrderStatus.PROCESSING);
    orderRepository.save(order);
    BigDecimal totalAmount = calculateTotalAmount(orderCreateRequest.getItems().stream().collect(Collectors.toMap(OrderItemCreateRequest::getQuantity, OrderItemCreateRequest::getPrice, BigDecimal::add)));
    saveOutboxEvent(orderCreateRequest,order, totalAmount);
  }


  private User findUserById(UUID userId) {
    return userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, userId));
  }

  private void saveOutboxEvent(OrderCreateRequest orderCreateRequest, Order order, BigDecimal totalAmount) {
    Map<UUID, Integer> productQuantities = orderCreateRequest.getItems().stream().collect(Collectors.toMap(OrderItemCreateRequest::getProductId, OrderItemCreateRequest::getQuantity));
    PaymentEvent paymentEvent = PaymentEvent.builder()
      .orderId(order.getId())
      .totalAmount(totalAmount)
      .paymentMethod(order.getPaymentMethod().toString())
      .productQuantities(productQuantities)
      .build();
    OutboxEvent paymentOutboxEvents = OutboxEvent.builder()
      .eventType("PAYMENT_EVENT")
      .payload(parseToJson.parseToJon(paymentEvent))
      .status(OutBoxEventStatus.PENDING).build();
    outBoxRepository.save(paymentOutboxEvents);
  }

  private Order buildOrder(OrderCreateRequest request, UUID userId, String idempotentKey) {
    Order order = mapper.toEntity(request);
    order.setUserId(userId);
    order.setStatus(OrderStatus.PENDING);
    order.setIdempotentKey(UUID.fromString(idempotentKey));
    return order;
  }

  private List<OrderItem> buildOrderItems(OrderCreateRequest orderCreateRequest, Long orderId) {
    List<OrderItem> orderItems = new ArrayList<>();

    for (OrderItemCreateRequest itemRequest : orderCreateRequest.getItems()) {

      OrderItem orderItem = OrderItem.builder().orderId(orderId).productId(itemRequest.getProductId()).quantity(itemRequest.getQuantity()).price(itemRequest.getPrice()).build();
      orderItems.add(orderItem);
    }
    return orderItems;
  }

  private BigDecimal calculateTotalAmount(Map<Integer, BigDecimal> orderItems) {
    return orderItems.entrySet().stream().map(entry -> entry.getValue().multiply(BigDecimal.valueOf(entry.getKey()))).reduce(BigDecimal.ZERO, BigDecimal::add);
  }


  private OrderResponse buildOrderResponse(Order order, UUID userId, List<OrderItem> orderItems) {
    return OrderResponse.builder().id(order.getId()).phoneNumber(order.getPhoneNumber()).customerName(order.getCustomerName()).address(order.getAddress()).paymentMethod(order.getPaymentMethod()).note(order.getNote()).userId(userId).createdDate(order.getCreatedDate()).idempotentKey(order.getIdempotentKey()).items(mapToOrderItemResponse(orderItems)).totalAmount(calculateTotalAmount(orderItems.stream().collect(Collectors.toMap(OrderItem::getQuantity, OrderItem::getPrice, BigDecimal::add)))).status(order.getStatus()).build();
  }

  private List<OrderItemResponse> mapToOrderItemResponse(List<OrderItem> items) {
    return items.stream().map(item -> {
      return OrderItemResponse.builder().productId(item.getProductId()).quantity(item.getQuantity()).price(item.getPrice()).build();
    }).toList();
  }

  @Override
  @Transactional
  public OrderResponse createOrder(OrderCreateRequest orderCreateRequest, String idempotentKey) {

    try {
      User user = findUserById(orderCreateRequest.getUserId());
      Order order = buildOrder(orderCreateRequest, user.getId(), idempotentKey);
      orderRepository.save(order);

      log.debug("[ORDER CREATE] -- [ORDER PAYLOAD]: {}", order);
      List<OrderItem> orderItems = buildOrderItems(orderCreateRequest, order.getId());
      orderItemRepository.saveAll(orderItems);
      log.debug("[ORDER CREATE] -- CREATED SUCCESSFULLY WITH ORDER ITEMS: {}", orderItems);

      return buildOrderResponse(order, user.getId(), orderItems);
    } catch (DataIntegrityViolationException e) {
      log.warn("[ORDER CREATE] -- Duplicate idempotentKey: {}", idempotentKey);
      return createOrder(orderCreateRequest, idempotentKey);
    }


  }

  @Override
  public OrderResponse checkIdempotency(OrderCreateRequest orderCreateRequest, String idempotentKey) {
    Optional<Order> existingOrder = orderRepository.findByIdempotentKey(UUID.fromString(idempotentKey));
    if (existingOrder.isPresent()) {
      log.info("[ORDER CREATE] -- Reusing existing order for idempotentKey: {}", idempotentKey);
      return buildOrderResponse(existingOrder.get(), existingOrder.get().getUserId(), buildOrderItems(orderCreateRequest, existingOrder.get().getId()));
    }
    return null;
  }
}