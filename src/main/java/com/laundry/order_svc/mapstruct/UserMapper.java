package com.laundry.order_svc.mapstruct;

import com.laundry.order_svc.dto.UserCreateRequest;
import com.laundry.order_svc.dto.UserResponse;
import com.laundry.order_svc.dto.UserUpdateRequest;
import com.laundry.order_svc.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
  //UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mapping(source = "fullname", target = "name")
  @Mapping(target = "userId", ignore = true)
    // bo qua mapping Id
  User toEntity(UserCreateRequest userCreateRequest);

  @Mapping(source = "name", target = "fullname")
  UserResponse toDTO(User user);

  // Phương thức này sẽ cập nhật các trường của user bằng dữ liệu từ userRequest
  @Mapping(source = "fullname", target = "name")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateUserFromRequest(UserUpdateRequest userUpdateRequest, @MappingTarget User user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
  void updateUserSetToNull(UserCreateRequest userCreateRequest, @MappingTarget User user);

}
