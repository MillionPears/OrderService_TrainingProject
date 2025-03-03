package com.laundry.order.service;

import com.laundry.order.entity.Order;

import java.math.BigDecimal;

public interface PaymentService {
  void createPayment(Order order, BigDecimal totalAmount);
}
