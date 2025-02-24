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
public class ProductCreateRequest {
  @NotNull
  @Column(unique = true)
  private String name;

  @NotNull
  private String description;

  @NotNull
  private BigDecimal price;

  @NotNull
  private Integer stockQuantity;

  private String category;
}
