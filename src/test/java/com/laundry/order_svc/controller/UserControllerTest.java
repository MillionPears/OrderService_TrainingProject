package com.laundry.order_svc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.laundry.order_svc.dto.UserCreateRequest;
import com.laundry.order_svc.dto.UserResponse;
import com.laundry.order_svc.enums.Gender;
import com.laundry.order_svc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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


@WebMvcTest(UserController.class)
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class UserControllerTest {
  private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);
  private final UUID userId = UUID.randomUUID();

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  private UserCreateRequest userCreateRequest;
  private UserResponse userResponse;
  private ObjectMapper objectMapper;
  @BeforeEach
  void init() {

    userCreateRequest = UserCreateRequest.builder()
      .fullname("Test5")
      .phoneNumber("1234567890")
      .dob(LocalDate.of(1995, 3, 2))
      .gender(Gender.MALE)
      .build();

    userResponse = UserResponse.builder()
      .userId(userId)
      .fullname("Test5")
      .phoneNumber("1234567890")
      .dob(LocalDate.now())
      .gender(Gender.MALE)
      .build();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void createUser_ShouldReturnCreatedStatus() throws Exception {
    // GIVEN
    String content = objectMapper.writeValueAsString(userCreateRequest);

    // WHEN
    when(userService.createUser(any(UserCreateRequest.class))).thenReturn(userResponse);

    // THEN
    mockMvc.perform(post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(content))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.data.userId").value(userId.toString()))
      .andExpect(jsonPath("$.data.fullname").value("Test5"))
      .andExpect(jsonPath("$.data.phoneNumber").value("1234567890"));
  }

  @Test
  @Disabled
  void createUser_ShouldReturnBadRequest_WhenUserRequestIsInValid() throws Exception {
    userCreateRequest.setFullname("");
    String content = objectMapper.writeValueAsString(userCreateRequest);
    log.info(content);
    mockMvc.perform(post("/api/v1/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(content))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$error").value("Bad Request"));
  }

}
