package com.laundry.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "inventory")
public class Inventory extends Auditor {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @OneToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @NotNull
  private Integer availableQuantity;

  @NotNull
  private Integer reservedQuantity = 0;

  @NotNull
  private Integer totalQuantity;

  @Version
  private Long version ;
}

