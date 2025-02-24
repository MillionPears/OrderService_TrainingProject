package com.laundry.order.service;

import com.laundry.order.dto.response.PaymentResponse;
import com.laundry.order.entity.Order;

import java.math.BigDecimal;

public interface PaymentService {
  void create(Order order, BigDecimal totalAmount);
}
