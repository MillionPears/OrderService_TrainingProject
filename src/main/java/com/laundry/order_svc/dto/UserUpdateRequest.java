package com.laundry.order_svc.dto;

import com.laundry.order_svc.enums.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserUpdateRequest {
  private String fullname;
  @Past
  private LocalDate dob;
  private Integer point;
  private Gender gender;
}
