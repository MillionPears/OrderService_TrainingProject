package com.laundry.order.service;

import com.laundry.order.dto.request.OrderCreateRequest;
import com.laundry.order.dto.response.OrderResponse;
import org.springframework.stereotype.Service;


public interface OrderService {
  OrderResponse createOrder(OrderCreateRequest orderCreateRequest);
}
