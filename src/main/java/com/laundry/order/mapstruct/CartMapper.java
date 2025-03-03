package com.laundry.order.mapstruct;

import com.laundry.order.dto.response.CartResponse;
import com.laundry.order.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
  @Mapping(source = "cart.id", target = "cartId")
  @Mapping(source = "cart.product.id", target = "productId")
  @Mapping(source = "cart.product.name", target = "productName")
  @Mapping(source = "cart.product.price", target = "price")
  @Mapping(source = "cart.quantity", target = "quantity")
  @Mapping(expression = "java(cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))", target = "totalPrice")
  CartResponse toDTO(Cart cart);
}
