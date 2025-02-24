package com.laundry.order.entity;


import com.laundry.order.enums.OrderStatus;
import com.laundry.order.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends Auditor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotNull
  private String phoneNumber;
  @NotBlank
  private String customerName;
  @NotNull
  @Size(min = 5, max = 255)
  private String address;
  @Enumerated(EnumType.STRING)
  private OrderStatus status;
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;
  private String note;

  @ManyToOne()
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> items;

  @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
  private Payment payment;
  @Version
  private Long version;
}
