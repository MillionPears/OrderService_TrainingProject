package com.laundry.order_svc.dto;

import com.laundry.order_svc.enums.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
//@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserCreateRequest {

  @NotNull(message = "Name cannot be null")
  @Size(min = 5, message = "Name must not be empty")
  private String fullname;
  @NotNull(message = "Date of birth cannot be null")
  @Past
  private LocalDate dob;

  @NotNull(message = "Phone number cannot be null")
  @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
  @Column(unique = true)
  private String phoneNumber;

  private Integer point;
  private Gender gender;


  public interface Create {
  }

  public interface Update {
  }
}
