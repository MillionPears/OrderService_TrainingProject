package com.laundry.order_svc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laundry.order_svc.enums.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequest {
    @NotNull(message = "Name cannot be null")
    @Size(min = 5, message = "Name must not be empty")
    private String fullname;

    @NotNull
    @Past
    private Date dob;

    @NotNull
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    @Column(unique = true)
    private String phoneNumber;

    private Gender gender;
}
