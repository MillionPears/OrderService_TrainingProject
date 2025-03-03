package com.laundry.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateRequest {
  @NotNull(message = "Product ID is required")
  private UUID productId;

  @Min(value = 1, message = "Quantity must be at least 1")
  private int quantity;

  @NotNull(message = "Price is required")
  private BigDecimal price;

}
