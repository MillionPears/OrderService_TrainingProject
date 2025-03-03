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
public class User extends AbstractVersionedEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id = UUID.randomUUID();

  @NotNull
  @Size(min = 5)
  @Column(name = "name", nullable = false)
  private String name;

  @NotNull
  @Past
  @Column(name = "dob", nullable = false)
  private LocalDate dob;

  @NotNull
  @Size(min = 10, max = 15)
  @Column(name = "phone_number", unique = true, nullable = false)
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", nullable = false)
  private Gender gender;

  @Column(name = "point")
  private Integer point;
}
