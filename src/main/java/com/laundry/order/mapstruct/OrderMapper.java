package com.laundry.order.mapstruct;

import com.laundry.order.dto.request.OrderCreateRequest;
import com.laundry.order.dto.response.OrderItemResponse;
import com.laundry.order.dto.response.OrderResponse;
import com.laundry.order.entity.Order;
import com.laundry.order.entity.OrderItem;
import com.laundry.order.entity.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
//  @Mapping(target = "user.id", source = "userId")
  Order toEntity(OrderCreateRequest orderCreateRequest);

  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "items", ignore = true)
  OrderResponse toDTO(Order order);

  @Mapping(target = "productName", source = "product.name")
  OrderItemResponse toOrderItemResponse(OrderItem orderItem);

}
