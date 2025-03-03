package com.laundry.order.service.implement;

import com.laundry.order.entity.Order;
import com.laundry.order.entity.Payment;
import com.laundry.order.enums.PaymentStatus;
import com.laundry.order.repository.OrderRepository;
import com.laundry.order.repository.PaymentRepository;
import com.laundry.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepository paymentRepository;
  private final OrderRepository orderRepository;
  @Override
  @Transactional
  public void createPayment(Order order, BigDecimal totalAmount) {
    if(order == null) throw new RuntimeException("order must be not null");
    Payment payment = Payment.builder()
      .order(order)
      .amount(totalAmount)
      .method(order.getPaymentMethod())
      .status(PaymentStatus.PENDING)
      .build();
    paymentRepository.save(payment);
  }
}
