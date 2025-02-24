package com.laundry.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product extends Auditor {

  @Id
  private UUID id = UUID.randomUUID();

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

  @Version
  private Long version = 0L;
}