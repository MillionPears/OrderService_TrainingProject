package com.laundry.order_svc.controller;

import com.laundry.order_svc.dto.ApiResponse;
import com.laundry.order_svc.dto.UserCreateRequest;
import com.laundry.order_svc.dto.UserResponse;
import com.laundry.order_svc.dto.UserUpdateRequest;
import com.laundry.order_svc.enums.Gender;
import com.laundry.order_svc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> createUser(
    @Valid @RequestBody UserCreateRequest userCreateRequest) {
    log.info("Class of userService: {}", userService.getClass().getName());
    UserResponse userResponse = userService.createUser(userCreateRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new ApiResponse<>(userResponse));
  }

  @PatchMapping(path = "/update/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID userId,
                                                              @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
    UserResponse userResponse = userService.updateUser(userId, userUpdateRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(new ApiResponse<>(userResponse));

  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id) {
    UserResponse userResponse = userService.getUserByUserId(id);
    return ResponseEntity.ok(new ApiResponse<>(userResponse));
  }

  @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponse<List<UserResponse>>> searchByName(
    @RequestParam String name,
    @PageableDefault(page = 0, size = 10) Pageable pageable) {
    List<UserResponse> list = userService.searchUserByName(name, pageable);
    if (list.isEmpty()) return ResponseEntity.noContent().build();
    return ResponseEntity.ok(new ApiResponse<>(list));
  }

  @GetMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<UserResponse>> searchAndFilterNotIndex(@RequestParam(required = false) String name,
                                                                    @RequestParam(required = false) Gender gender,
                                                                    @RequestParam(defaultValue = "name") String sortBy,
                                                                    @RequestParam(defaultValue = "ASC") String sortDirection,
                                                                    Pageable pageable) {
    Page<UserResponse> userResponsePage = userService.searchAndFilterNotIndex(name, gender, sortBy, sortDirection, pageable);
    return ResponseEntity.ok(userResponsePage);
  }

  @GetMapping(path = "/filter/index", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<UserResponse>> searchAndFilterWithIndex(@RequestParam(required = false) String name,
                                                                     @RequestParam(required = false) Gender gender,
                                                                     @RequestParam(defaultValue = "name") String sortBy,
                                                                     @RequestParam(defaultValue = "ASC") String sortDirection,
                                                                     Pageable pageable) {
    Page<UserResponse> userResponsePage = userService.searchAndFilterWithIndex(name, gender, sortBy, sortDirection, pageable);
    return ResponseEntity.ok(userResponsePage);
  }
}
