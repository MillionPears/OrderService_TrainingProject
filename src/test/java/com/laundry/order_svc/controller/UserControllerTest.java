package com.laundry.order_svc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.laundry.order_svc.dto.UserRequest;
import com.laundry.order_svc.dto.UserResponse;
import com.laundry.order_svc.enums.Gender;
import com.laundry.order_svc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Import(TestConfig.class)
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(UserController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
  private final UUID userId = UUID.randomUUID();
  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private UserService userService;
  private UserRequest userRequest;
  private UserResponse userResponse;

  @BeforeEach
  void init() {

    userRequest = UserRequest.builder()
      .fullname("Test")
      .phoneNumber("1234567890")
      .dob(LocalDate.now())
      .gender(Gender.MALE)
      .build();

    userResponse = UserResponse.builder()
      .userId(userId)
      .fullname("Test")
      .phoneNumber("1234567890")
      .dob(LocalDate.now())
      .gender(Gender.MALE)
      .build();
  }

  @Test
    // success
  void createUser_ShouldReturnCreatedStatus() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String content = objectMapper.writeValueAsString(userRequest);

    // WHEN
    when(userService.createUser(any(UserRequest.class))).thenReturn(userResponse);


    // THEN
    mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(content))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.data.userId").value(userId.toString()))
      .andExpect(jsonPath("$.data.fullname").value("Test"))
      .andExpect(jsonPath("$.data.phoneNumber").value("1234567890"));

  }
}
