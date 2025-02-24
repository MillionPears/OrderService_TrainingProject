package com.laundry.order.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.laundry.order.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCreateRequest {

  @NotNull(message = "Phone number cannot be null")
  @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
  private String phoneNumber;

  @NotNull(message = "Customer name cannot be null")
  @Size(min = 5, message = "Customer name must be at least 5 characters")
  private String customerName;

  @NotNull(message = "Address cannot be null")
  @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
  private String address;

  @NotNull(message = "Payment method cannot be null")
  private PaymentMethod paymentMethod;

  private String note;
  private List<OrderItemCreateRequest> items;

  @NotNull(message = "User ID cannot be null")
  private UUID userId;
}
