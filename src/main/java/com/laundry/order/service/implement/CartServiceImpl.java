package com.laundry.order.service.implement;

import com.laundry.order.dto.response.CartResponse;
import com.laundry.order.entity.Cart;
import com.laundry.order.entity.Product;
import com.laundry.order.exception.CustomException;
import com.laundry.order.exception.ErrorCode;
import com.laundry.order.mapstruct.CartMapper;
import com.laundry.order.repository.CartRepository;
import com.laundry.order.repository.ProductRepository;
import com.laundry.order.repository.UserRepository;
import com.laundry.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CartMapper mapper;

  @Override
  @Transactional
  public CartResponse addToCart(UUID productId, int quantity, UUID userId) {
    Product product = productRepository.findById(productId).orElseThrow(
      ()-> new CustomException(ErrorCode.NOT_FOUND,"Product",productId)
    );

    Cart cart = Cart.builder()
      .user(userRepository.findById(userId).orElseThrow())
      .product(product)
      .quantity(quantity)
      .build();
    cart = cartRepository.save(cart);
    return mapper.toDTO(cart);
  }
}
