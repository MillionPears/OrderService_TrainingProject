package com.laundry.order.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
  private String name;

  private String description;

  private BigDecimal price;

  private Integer stockQuantity;

  private String category;
}
