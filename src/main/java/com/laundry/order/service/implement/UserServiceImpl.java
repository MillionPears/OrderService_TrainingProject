package com.laundry.order.service.implement;

import com.laundry.order.dto.request.UserCreateRequest;
import com.laundry.order.dto.response.UserResponse;
import com.laundry.order.dto.request.UserUpdateRequest;
import com.laundry.order.entity.User;
import com.laundry.order.enums.Gender;
import com.laundry.order.exception.CustomException;
import com.laundry.order.exception.ErrorCode;
import com.laundry.order.mapstruct.UserMapper;
import com.laundry.order.repository.UserRepository;
import com.laundry.order.repository.specification.UserSpecification;
import com.laundry.order.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final TransactionTemplate transactionTemplate;

  @Override
  @Transactional()
  public UserResponse createUser( UserCreateRequest userCreateRequest) {
    User user = userMapper.toEntity(userCreateRequest);
    if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) throw new CustomException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS,user.getPhoneNumber());
    user = userRepository.save(user);

    return
      userMapper.toDTO(user);
  }

  @Override
  public UserResponse getUserByUserId(UUID userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND,userId));
    return userMapper.toDTO(user);
  }


//@Override
//@Transactional
//public UserResponse updateUserById(UUID userId, UserUpdateRequest userUpdateRequest) {
//    User user = userRepository.findById(userId)
//      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
//    userMapper.updateUserFromRequest(userUpdateRequest, user);
//    if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) throw new CustomException(ErrorCode.CONFLICT);
//    user = userRepository.save(user);
//    return userMapper.toDTO(user);
//}

  @Override
  public UserResponse updateUserById(UUID userId, UserUpdateRequest userUpdateRequest) {
    return transactionTemplate.execute(status -> {
      User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND,userId));
      userMapper.updateUserFromRequest(userUpdateRequest, user);
      user = userRepository.save(user);
      return userMapper.toDTO(user);
    });
  }

  @Override
  public List<UserResponse> searchUserByName(String name, Pageable pageable) {
    Specification<User> specification = Specification.where(UserSpecification.hasName(name));
    Page<User> list = userRepository.findAll(specification, pageable);
    return list.getContent().stream()
      .map(userMapper::toDTO)
      .collect(Collectors.toList());
  }

  @Override
  public List<UserResponse> filterUserByGender(Gender gender, Pageable pageable) {
    Specification<User> specification = Specification.where(UserSpecification.hasGender(gender));
    Page<User> list = userRepository.findAll(specification, pageable);
    return list.getContent().stream()
      .map(userMapper::toDTO)
      .collect(Collectors.toList());
  }

//  @Override
//  public Page<UserResponse> filterUserByNameAndGender(String name, Gender gender, String sortBy, String sortDirection, Pageable pageable) {
//    Specification<User> specification = Specification
//      .where(UserSpecification.hasGender(gender))
//      .and(UserSpecification.hasName(name));
//    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
//    Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//    Page<User> userPage = userRepository.findAll(specification, pageRequest);
//    return userPage.map(userMapper::toDTO);
//  }

  @Override
  public Page<UserResponse> filterUserByNameAndGender(String name,
                                                     String gender,
                                                     String sortBy,
                                                     String sortDirection,
                                                     Pageable pageable) {
//    if (!sortBy.matches("name|gender")) {
//      throw new CustomException(ErrorCode.BAD_REQUEST);
//    }
//    if (!sortDirection.equalsIgnoreCase("ASC") && !sortDirection.equalsIgnoreCase("DESC")) {
//      throw new CustomException(ErrorCode.BAD_REQUEST);
//    }
    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
    Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    Page<User> userPage = userRepository.filterUserByNameAndGender(gender, name, pageRequest);
    return userPage.map(userMapper::toDTO);
  }

  @Override
  public int updateUserPointByListUserIds(Integer point, List<UUID> userIds) {

    return userRepository.updateUserPointByListId(point, userIds);
  }

}
