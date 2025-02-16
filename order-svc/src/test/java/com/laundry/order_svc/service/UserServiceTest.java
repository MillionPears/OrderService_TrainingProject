package com.laundry.order_svc.service;

import com.laundry.order_svc.dto.UserRequest;
import com.laundry.order_svc.entity.User;
import com.laundry.order_svc.enums.Gender;
import com.laundry.order_svc.exception.CustomException;
import com.laundry.order_svc.mapstruct.UserMapper;
import com.laundry.order_svc.repository.UserRepository;
import com.laundry.order_svc.service.implement.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ShouldThrowCustomException_WhenPhoneNumberExists(){
        // Arrange
        UserRequest userRequest = UserRequest.builder()
                .phoneNumber("123456789")
                .build();
        User user = new User();
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        user.setPhoneNumber(userRequest.getPhoneNumber());
        when(userRepository.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(true);
        //Act and Assert
        assertThrows(CustomException.class, () -> userService.createUser(userRequest));
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void createUser_ShouldCreateUser_WhenPhoneNumberDoesNotExits(){

    }
}
