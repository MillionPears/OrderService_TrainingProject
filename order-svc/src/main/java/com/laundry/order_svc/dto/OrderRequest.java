package com.laundry.order_svc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laundry.order_svc.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)   // Do not serialize null fields
public class OrderRequest {


    @NotNull
    @Column(unique = true)
    private String phoneNumber;

    @NotNull
    @Size(min = 5, max = 255)
    private String address;

    private OrderStatus status;
    private String note;

    @NotNull
    private UUID userId;

}
