package com.laundry.order.service;

import com.laundry.order.dto.request.OrderCreateRequest;
import com.laundry.order.dto.response.OrderResponse;


public interface OrderService {
  OrderResponse createOrder(OrderCreateRequest orderCreateRequest, String idempotentKey);
  //List<ProductResponse> getProductById(List<UUID> productIds);
}
