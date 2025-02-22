package com.laundry.order_svc.service;

import com.laundry.order_svc.dto.UserCreateRequest;
import com.laundry.order_svc.dto.UserResponse;
import com.laundry.order_svc.dto.UserUpdateRequest;
import com.laundry.order_svc.entity.User;
import com.laundry.order_svc.enums.Gender;
import com.laundry.order_svc.exception.CustomException;
import com.laundry.order_svc.exception.ErrorCode;
import com.laundry.order_svc.mapstruct.UserMapper;
import com.laundry.order_svc.repository.UserRepository;
import com.laundry.order_svc.service.implement.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock
  private UserMapper userMapper;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;
  private final UUID userId = UUID.randomUUID();

  @Test
  void createUser_ShouldThrowCustomException_WhenPhoneNumberExists() {
    // Arrange
    UserCreateRequest userCreateRequest = UserCreateRequest.builder()
      .phoneNumber("123456789")
      .build();
    User user = new User();
    when(userMapper.toEntity(userCreateRequest)).thenReturn(user);
    user.setPhoneNumber(userCreateRequest.getPhoneNumber());
    when(userRepository.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(true);
    //Act and Assert
    CustomException exception = assertThrows(CustomException.class, () -> userService.createUser(userCreateRequest));
    assertEquals(ErrorCode.CONFLICT, exception.getErrorCode());
    verify(userRepository, never()).save(any(User.class));

  }

  @Test
  void createUser_ShouldCreateUser_WhenPhoneNumberDoesNotExits() {
    UserCreateRequest userCreateRequest = UserCreateRequest.builder()
      .phoneNumber("123456789")
      .build();
    User user = new User();
    when(userMapper.toEntity(userCreateRequest)).thenReturn(user);
    user.setPhoneNumber(userCreateRequest.getPhoneNumber());
    when(userRepository.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(false);

    User savedUser = new User();
    savedUser.setUserId(userId);
    savedUser.setPhoneNumber(user.getPhoneNumber());
    when(userRepository.save(user)).thenReturn(savedUser);

    UserResponse userResponse = UserResponse
      .builder()
      .userId(userId)
      .phoneNumber(savedUser.getPhoneNumber())
      .build();

    when(userMapper.toDTO(savedUser)).thenReturn(userResponse);

    UserResponse result = userService.createUser(userCreateRequest);

    assertNotNull(result);
    assertEquals(userCreateRequest.getPhoneNumber(),userResponse.getPhoneNumber());

    verify(userMapper).toEntity(userCreateRequest);
    verify(userRepository).existsByPhoneNumber(user.getPhoneNumber());
    verify(userRepository).save(user);
    verify(userMapper).toDTO(savedUser);

  }

  @Test
  void updateUser_ShouldThrowCustomException_WhenUserNotFound(){
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder().build();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());
    CustomException exception = assertThrows(CustomException.class,
      ()-> userService.updateUser(userId, userUpdateRequest));
    assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    verify(userRepository).findById(userId);
  }

  @Test
  void updateUser_ShouldThrowCustomException_WhenOptimisticLockingFailureOccurs(){
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder().build();
    User user = new User();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    doNothing().when(userMapper).updateUserFromRequest(userUpdateRequest, user);
    when(userRepository.save(user)).thenThrow(OptimisticLockingFailureException.class);

    CustomException exception = assertThrows(CustomException.class
      ,() -> userService.updateUser(userId, userUpdateRequest) );
    assertEquals(ErrorCode.CONFLICT, exception.getErrorCode());
    verify(userRepository).findById(userId);
    verify(userMapper).updateUserFromRequest(userUpdateRequest, user);
    verify(userRepository).save(user);
  }

  @Test
  void updateUser_ShouldReturnUserResponse_WhenUserExists() {
    UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder().build();
    User user = new User();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    doNothing().when(userMapper).updateUserFromRequest(userUpdateRequest, user);
    when(userRepository.save(user)).thenReturn(user);
    UserResponse userResponse = UserResponse.builder().build();
    when(userMapper.toDTO(user)).thenReturn(userResponse);

    UserResponse result = userService.updateUser(userId, userUpdateRequest);
    assertEquals(userResponse, result);
    verify(userRepository).findById(userId);
    verify(userMapper).updateUserFromRequest(userUpdateRequest, user);
    verify(userRepository).save(user);
    verify(userMapper).toDTO(user);
  }

  private String name;
  private Gender gender;
  private String sortBy;
  private String sortDirection;
  private Pageable pageable;
  @BeforeEach
  void setup(){
     name = "John";
     gender = Gender.MALE;
     sortBy = "name";
     sortDirection = "ASC";
     pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
  }

  @Test
  void searchAndFilterWithIndex_ShouldHandleInvalidSortBy(){
    String invalidSortBy = "GENDER";
    CustomException exception = assertThrows(CustomException.class,
      ()-> userService.searchAndFilterWithIndex(name,gender,invalidSortBy, sortDirection, pageable));
    assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void searchAndFilterWithIndex_ShouldHandleInvalidSortDirection() {
    String invalidSortDirection = "INVALID";
    CustomException exception = assertThrows(CustomException.class,
      ()-> userService.searchAndFilterWithIndex(name,gender,sortBy, invalidSortDirection, pageable));
    assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void searchAndFilterWithIndex_ShouldReturnEmptyPage_WhenNoUsersMatch() {
    Page<User> userPage = Page.empty();
    when(userRepository.filterByNameAndGenderWithIndex(name, gender, pageable)).thenReturn(userPage);
    Page<UserResponse> result = userService.searchAndFilterWithIndex(name, gender, sortBy, sortDirection, pageable);
    assertTrue(result.isEmpty());
    verify(userRepository).filterByNameAndGenderWithIndex(name, gender, pageable);
    verify(userMapper,never()).toDTO(any());
  }

  @Test
  void searchAndFilterWithIndex_ShouldReturnPageOfUserResponse_WhenValidInputs(){
    User user = new User();
    UserResponse userResponse = UserResponse.builder().build();
    Page<User> userPage = new PageImpl<>(List.of(user),pageable,1);
    when(userRepository.filterByNameAndGenderWithIndex(name, gender, pageable))
      .thenReturn(userPage);
    when(userMapper.toDTO(user)).thenReturn(userResponse);
    Page<UserResponse> result = userService.searchAndFilterWithIndex(name, gender, sortBy, sortDirection, pageable);
    assertEquals(1, result.getTotalElements());
    assertEquals(userResponse, result.getContent().getFirst());
    verify(userRepository).filterByNameAndGenderWithIndex(name, gender, pageable);
    verify(userMapper).toDTO(user);
  }
}
