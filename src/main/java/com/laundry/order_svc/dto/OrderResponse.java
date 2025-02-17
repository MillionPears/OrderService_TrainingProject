package com.laundry.order_svc.dto;

import com.laundry.order_svc.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class OrderResponse {

  private UUID orderId;
  private LocalDateTime createdDate;
  private String phoneNumber;
  private String address;
  private OrderStatus status;
  private String note;
  private String userId;
}
