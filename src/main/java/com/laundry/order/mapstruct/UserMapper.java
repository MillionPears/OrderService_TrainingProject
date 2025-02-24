package com.laundry.order.mapstruct;

import com.laundry.order.dto.request.UserCreateRequest;
import com.laundry.order.dto.response.UserResponse;
import com.laundry.order.dto.request.UserUpdateRequest;
import com.laundry.order.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
  //UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
  User toEntity(UserCreateRequest userCreateRequest);
  UserResponse toDTO(User user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateUserFromRequest(UserUpdateRequest userUpdateRequest, @MappingTarget User user);


}
