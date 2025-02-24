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
import com.laundry.order.repository.*;
import com.laundry.order.service.OrderService;
import com.laundry.order.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final OrderItemRepository orderItemRepository;
  private final PaymentService paymentService;
  private final OrderMapper orderMapper;

  @Override
  @Transactional
  public OrderResponse createOrder(OrderCreateRequest orderCreateRequest) {
    Order order = orderMapper.toEntity(orderCreateRequest);
    User user = userRepository.findById(orderCreateRequest.getUserId())
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    order.setUser(user);
    order.setStatus(OrderStatus.PENDING);
    order.setItems(new ArrayList<>());    for (OrderItemCreateRequest orderItemCreateRequest: orderCreateRequest.getItems()){
      Product product = productRepository.findById(orderItemCreateRequest.getProductId()).orElseThrow(
        ()-> new CustomException(ErrorCode.NOT_FOUND)
      );

      if(product.getStockQuantity()< orderItemCreateRequest.getQuantity()) {
        throw new RuntimeException("Not enough stock for product: "+product.getName());
      }

      product.setStockQuantity(product.getStockQuantity()-orderItemCreateRequest.getQuantity());

      OrderItem orderItem = OrderItem.builder()
        .order(order)
        .product(product)
        .quantity(orderItemCreateRequest.getQuantity())
        .price(orderItemCreateRequest.getPrice())
        .build();
      order.getItems().add(orderItem);
      }
    order = orderRepository.save(order);
    BigDecimal totalAmount = calculateTotalAmount(order);
    paymentService.create(order, totalAmount);
    System.out.println("haha" + order.getItems().getFirst().getProduct().getName());
    return mapToOrderResponse(order,totalAmount);
  }
  private BigDecimal calculateTotalAmount(Order order) {
    return order.getItems().stream()
      .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private OrderResponse mapToOrderResponse(Order order, BigDecimal totalAmount) {
    OrderResponse response =  OrderResponse.builder()
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
