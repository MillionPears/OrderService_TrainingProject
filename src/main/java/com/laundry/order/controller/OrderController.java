package com.laundry.order.controller;

import com.laundry.order.dto.ApiResponse;
import com.laundry.order.dto.request.OrderCreateRequest;
import com.laundry.order.dto.response.OrderResponse;
import com.laundry.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OrderResponse>> createOrder( @Valid  @RequestBody OrderCreateRequest orderCreateRequest) {
    OrderResponse orderResponse = orderService.createOrder(orderCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new ApiResponse<>(orderResponse));
  }

}
