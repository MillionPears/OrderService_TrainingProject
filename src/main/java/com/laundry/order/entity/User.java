package com.laundry.order.entity;

import com.laundry.order.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends Auditor {
  @Id
  private UUID id = UUID.randomUUID();

  @NotNull()
  @Size(min = 5)
  private String name;

  @NotNull()
  @Past
  private LocalDate dob;

  @NotNull()
  @Size(min = 10, max = 15)
  @Column(unique = true)
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private Integer point;

  @Version
  private Long version ;
}
