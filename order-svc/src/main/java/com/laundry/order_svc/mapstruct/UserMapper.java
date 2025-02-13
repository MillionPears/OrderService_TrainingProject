package com.laundry.order_svc.mapstruct;

import com.laundry.order_svc.dto.UserRequest;
import com.laundry.order_svc.dto.UserResponse;
import com.laundry.order_svc.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface UserMapper {
    //UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "fullname", target = "name")
    @Mapping(target = "userId", ignore = true)
    User toEntity (UserRequest userRequest);

    @Mapping(source = "name",target = "fullname")
    UserResponse toDTO(User user);

    // Phương thức này sẽ cập nhật các trường của user bằng dữ liệu từ userRequest
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UserRequest userRequest, @MappingTarget User user);


}
