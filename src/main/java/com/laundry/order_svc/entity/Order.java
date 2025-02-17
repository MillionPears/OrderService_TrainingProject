package com.laundry.order_svc.entity;


import com.laundry.order_svc.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends AuditorExample {
  @Id
  private UUID orderId = UUID.randomUUID();


  @NotNull
  @Column()
  private String phoneNumber;

  @NotNull
  @Size(min = 5, max = 255)
  private String address;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  private String note;

  @ManyToOne()
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  @Version
  private Long version = 0L;
}
