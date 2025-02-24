package com.laundry.order.service;

import com.laundry.order.dto.response.CartResponse;
import com.laundry.order.entity.Cart;

import java.util.List;
import java.util.UUID;

public interface CartService {
  CartResponse addToCart(UUID productId, int quantity, UUID userId);
}
