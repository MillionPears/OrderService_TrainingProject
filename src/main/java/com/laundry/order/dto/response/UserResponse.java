package com.laundry.order.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.laundry.order.enums.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
public class UserResponse {

  private UUID id;
  private String name;
  private LocalDate dob;
  private String phoneNumber;
  private Gender gender;
  private Integer point;
  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;
}
