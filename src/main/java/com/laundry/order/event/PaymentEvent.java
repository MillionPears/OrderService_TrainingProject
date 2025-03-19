package com.laundry.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent {
  private Long orderId;
  private BigDecimal totalAmount;
  private String paymentMethod;
  private Map<UUID,Integer> productQuantities;
}
