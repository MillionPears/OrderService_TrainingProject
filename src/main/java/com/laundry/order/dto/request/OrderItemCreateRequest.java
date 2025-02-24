package com.laundry.order.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateRequest {
  private UUID productId;
  private Integer quantity;
  private BigDecimal price;
}
