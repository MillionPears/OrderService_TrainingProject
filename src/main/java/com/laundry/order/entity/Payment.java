package com.laundry.order.entity;

import com.laundry.order.enums.PaymentMethod;
import com.laundry.order.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "payments")
public class Payment extends Auditor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @NotNull
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  private PaymentMethod method;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  private LocalDateTime paymentDate;

  private String transactionId;
}
