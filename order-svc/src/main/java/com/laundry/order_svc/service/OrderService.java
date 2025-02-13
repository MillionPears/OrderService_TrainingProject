package com.laundry.order_svc.service;

import com.laundry.order_svc.dto.OrderRequest;
import com.laundry.order_svc.dto.OrderResponse;


public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

}
