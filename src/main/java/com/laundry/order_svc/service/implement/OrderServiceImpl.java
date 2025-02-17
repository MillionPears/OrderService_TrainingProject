package com.laundry.order_svc.service.implement;

import com.laundry.order_svc.dto.OrderRequest;
import com.laundry.order_svc.dto.OrderResponse;
import com.laundry.order_svc.entity.Order;
import com.laundry.order_svc.entity.User;
import com.laundry.order_svc.enums.OrderStatus;
import com.laundry.order_svc.exception.CustomException;
import com.laundry.order_svc.exception.ErrorCode;
import com.laundry.order_svc.mapstruct.OrderMapper;
import com.laundry.order_svc.repository.OrderRepository;
import com.laundry.order_svc.repository.UserRepository;
import com.laundry.order_svc.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final UserRepository userRepository;
  private final OrderMapper orderMapper;

  @Override
  @Transactional
  public OrderResponse createOrder(OrderRequest orderRequest) {
    Order order = orderMapper.toEntity(orderRequest);
    User user = userRepository.findById(orderRequest.getUserId())
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    order.setUser(user);
    order.setStatus(OrderStatus.PENDING);
    //order.setCreatedDate(LocalDateTime.now());
    order = orderRepository.save(order);
    OrderResponse orderResponse = orderMapper.toDTO(order);
    orderResponse.setUserId(String.valueOf(order.getUser().getUserId()));
    return orderResponse;
  }
}
