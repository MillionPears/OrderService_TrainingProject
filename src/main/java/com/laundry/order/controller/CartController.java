package com.laundry.order.controller;

import com.laundry.order.dto.ApiResponse;
import com.laundry.order.dto.response.CartResponse;
import com.laundry.order.service.CartService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @PostMapping("/add")
  public ResponseEntity<ApiResponse<CartResponse>> addToCart(@RequestParam UUID productId,
                                                             @RequestParam @Min(1) int quantity,
                                                             @RequestParam UUID userId){
    CartResponse cartResponse = cartService.addToCart(productId, quantity, userId);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new ApiResponse<>(cartResponse));
  }



}
