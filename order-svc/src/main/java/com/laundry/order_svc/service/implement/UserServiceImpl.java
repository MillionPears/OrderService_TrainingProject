package com.laundry.order_svc.service.implement;

import com.laundry.order_svc.dto.UserRequest;
import com.laundry.order_svc.dto.UserResponse;
import com.laundry.order_svc.entity.Order;
import com.laundry.order_svc.entity.User;
import com.laundry.order_svc.enums.Gender;
import com.laundry.order_svc.exception.CustomException;
import com.laundry.order_svc.exception.ErrorCode;
import com.laundry.order_svc.mapstruct.UserMapper;
import com.laundry.order_svc.repository.UserRepository;
import com.laundry.order_svc.repository.specification.UserSpecification;
import com.laundry.order_svc.service.UserService;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserResponse createUser(UserRequest userRequest) {
            User user = userMapper.toEntity(userRequest);
            user = userRepository.save(user);
            System.out.println(user.getName());
            return userMapper.toDTO(user);
    }

    @Override
    public UserResponse getUserByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND));
        user.getOrders().stream()
                .forEach(order -> {
                    System.out.println(order.getPhoneNumber());
                });

        return userMapper.toDTO(user);
    }


//    @Override
//    @Transactional
//    public UserResponse updateUser(UUID userId, Map<String, Object> updates) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
//        updates.forEach((key, value) -> {
//            try {
//                String setterMethodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1); // "fullname" -> setFullname
//                Method setterMethod = UserRequest.class.getMethod(setterMethodName, value.getClass());  // setFullname -> getFullname
//                user = userMapper.toEntity(setterMethod.invoke(new UserRequest(), value));
//
//            } catch (Exception e) {
//                // Xử lý trường hợp không tìm thấy setter hoặc có lỗi khi gọi setter
//                // Ví dụ, log lỗi hoặc ném exception tùy theo yêu cầu
//            }
//        });
//        userRepository.save(user);
//        return userMapper.toDTO(user);
//    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID userId, UserRequest userRequest) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
            userMapper.updateUserFromRequest(userRequest, user);
            userRepository.save(user);
            return userMapper.toDTO(user);
        } catch (OptimisticLockingFailureException e) {
            throw new CustomException(ErrorCode.CONFLICT);
        }
    }

    @Override
    public List<UserResponse> searchUserByName(String name, Pageable pageable) {
        Specification<User> specification = Specification.where(UserSpecification.hasName(name));
        Page<User> list = userRepository.findAll(specification,pageable);
        return list.getContent().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> filterUserByGender(Gender gender,Pageable pageable) {
        Specification<User> specification = Specification.where(UserSpecification.hasGender(gender));
        Page<User> list = userRepository.findAll(specification,pageable);
        return list.getContent().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }


}
