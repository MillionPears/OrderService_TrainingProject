package com.laundry.order.dto.request;

import com.laundry.order.enums.Gender;
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
  @Size(min = 5)
  private String name;
  @Past
  private LocalDate dob;
  private Integer point;
  private Gender gender;
}
