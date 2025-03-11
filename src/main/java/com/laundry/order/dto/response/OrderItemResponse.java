package com.laundry.order.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
  private UUID productId;
  private Integer quantity;
  private BigDecimal price;
}
