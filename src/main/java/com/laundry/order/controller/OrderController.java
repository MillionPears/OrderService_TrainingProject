package com.laundry.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.laundry.order.dto.ApiResponse;
import com.laundry.order.dto.request.OrderCreateRequest;
import com.laundry.order.dto.response.OrderResponse;
import com.laundry.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;
  private final HttpServletRequest request;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
    @Valid @RequestBody OrderCreateRequest orderCreateRequest
    , @RequestHeader("Idempotent-Key") String idempotentKey
  ) throws JsonProcessingException {
    log.debug("[CREATE ORDER] [CONTROLLER] - Received request: [IP = {}], [IdempotentKey ={}]",
      request.getRemoteAddr(), idempotentKey);
    log.trace("This is a TRACE log to check if Log4j2 is working.");
    OrderResponse orderResponse = orderService.createOrder(orderCreateRequest, idempotentKey);
    log.info("[CREATE ORDER] [SUCCESS] - Successfully: [OrderId ={}] ", orderResponse.getId());
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new ApiResponse<>(orderResponse));
  }

//  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//  public ResponseEntity<ApiResponse<List<ProductResponse>>>getProductByIds(@RequestParam List<UUID> productIds){
//    return ResponseEntity.ok(new ApiResponse<>(orderService.getProductById(productIds)));
//  }

}
