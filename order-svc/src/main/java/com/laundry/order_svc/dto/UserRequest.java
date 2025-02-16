package com.laundry.order_svc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laundry.order_svc.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserRequest {

    public interface Create {}
    public interface Update {}

    @NotNull(message = "Name cannot be null", groups = Create.class)
    @Size(min = 5, message = "Name must not be empty")
    private String fullname;

    @NotNull(message = "Date of birth cannot be null", groups = Create.class)
    @Past
    private LocalDate dob;

    @NotNull(message = "Phone number cannot be null", groups = Create.class)
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    @Column(unique = true)
    private String phoneNumber;

    private Integer point;
    private Gender gender;
}
