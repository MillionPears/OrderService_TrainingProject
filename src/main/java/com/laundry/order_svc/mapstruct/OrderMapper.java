package com.laundry.order_svc.mapstruct;

import com.laundry.order_svc.dto.OrderCreateRequest;
import com.laundry.order_svc.dto.OrderResponse;
import com.laundry.order_svc.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  //OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);


  @Mapping(target = "orderId", ignore = true)
  Order toEntity(OrderCreateRequest orderCreateRequest);

  OrderResponse toDTO(Order order);


}
