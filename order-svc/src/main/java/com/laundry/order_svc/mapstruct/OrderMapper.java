package com.laundry.order_svc.mapstruct;

import com.laundry.order_svc.dto.OrderRequest;
import com.laundry.order_svc.dto.OrderResponse;
import com.laundry.order_svc.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    //OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);


    @Mapping(target = "orderId", ignore = true)
    Order toEntity(OrderRequest orderRequest);
    OrderResponse toDTO (Order order);


}
