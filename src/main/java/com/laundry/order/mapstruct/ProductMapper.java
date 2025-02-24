package com.laundry.order.mapstruct;

import com.laundry.order.dto.request.ProductCreateRequest;
import com.laundry.order.dto.request.ProductUpdateRequest;
import com.laundry.order.dto.request.UserUpdateRequest;
import com.laundry.order.dto.response.ProductResponse;
import com.laundry.order.entity.Product;
import com.laundry.order.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  Product toEntity(ProductCreateRequest productCreateRequest);
  ProductResponse toDTO(Product product);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void setProductUpdateRequest(ProductUpdateRequest productUpdateRequest, @MappingTarget Product product);

}
