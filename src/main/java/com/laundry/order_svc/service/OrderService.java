package com.laundry.order_svc.service;

import com.laundry.order_svc.dto.OrderCreateRequest;
import com.laundry.order_svc.dto.OrderResponse;


public interface OrderService {
  OrderResponse createOrder(OrderCreateRequest orderCreateRequest);

}
