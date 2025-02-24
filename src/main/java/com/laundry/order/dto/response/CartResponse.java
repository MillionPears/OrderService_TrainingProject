package com.laundry.order.dto.response;

import com.laundry.order.entity.Product;
import com.laundry.order.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
  private Long cartId;
  private UUID productId;
  private String productName;
  private String category;
  private BigDecimal price;
  private Integer quantity;
  private BigDecimal totalPrice;
}

