package com.laundry.order.entity;

import com.laundry.order.enums.OrderStatus;
import com.laundry.order.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order extends AbstractVersionedEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  @NotBlank
  @Column(name = "customer_name", nullable = false)
  private String customerName;

  @NotNull
  @Size(min = 5, max = 255)
  @Column(name = "address", nullable = false, length = 255)
  private String address;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private OrderStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false)
  private PaymentMethod paymentMethod;

  @Column(name = "note")
  private String note;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @NotNull
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  @Column(name = "idempotent_key", unique = true)
  private UUID idempotentKey;
}
