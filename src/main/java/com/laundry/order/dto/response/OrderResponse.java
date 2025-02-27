package com.laundry.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laundry.order.enums.OrderStatus;
import com.laundry.order.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
  private Long id;
  private String phoneNumber;
  private String customerName;
  private String address;
  private OrderStatus status;
  private PaymentMethod paymentMethod;
  private String note;
  private UUID userId;
  private LocalDateTime createdDate;
  private List<OrderItemResponse> items;
  private UUID idempotentKey;
  private BigDecimal totalAmount;
}
