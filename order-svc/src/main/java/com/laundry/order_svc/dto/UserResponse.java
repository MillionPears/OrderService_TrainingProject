package com.laundry.order_svc.dto;

import com.laundry.order_svc.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
public class UserResponse {


    private UUID userId;
    private String fullname;
    private Date dob;
    private String phoneNumber;
    private Gender gender;
}
