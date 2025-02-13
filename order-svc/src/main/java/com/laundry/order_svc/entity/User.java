package com.laundry.order_svc.entity;

import com.laundry.order_svc.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID userId = UUID.randomUUID();

    @NotNull
    @Size(min = 5)
    private String name;

    @NotNull
    @Past
    private Date dob;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
