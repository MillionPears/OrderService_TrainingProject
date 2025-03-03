package com.laundry.order.controller;

import com.laundry.order.dto.ApiResponse;
import com.laundry.order.dto.request.UserCreateRequest;
import com.laundry.order.dto.response.UserResponse;
import com.laundry.order.dto.request.UserUpdateRequest;
import com.laundry.order.service.UserService;
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
    UserResponse userResponse = userService.updateUserById(userId, userUpdateRequest);
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

//  @GetMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
//  public ResponseEntity<Page<UserResponse>> searchAndFilterNotIndex(@RequestParam(required = false) String name,
//                                                                    @RequestParam(required = false) String gender,
//                                                                    @RequestParam(defaultValue = "name") String sortBy,
//                                                                    @RequestParam(defaultValue = "ASC") String sortDirection,
//                                                                    Pageable pageable) {
//    Page<UserResponse> userResponsePage = userService.filterUserByNameAndGender(name, gender, sortBy, sortDirection, pageable);
//    return ResponseEntity.ok(userResponsePage);
//  }

  @GetMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<UserResponse>> filterUserByNameAndGender(@RequestParam(required = false) String name,
                                                                     @RequestParam(required = false) String gender,
                                                                     @RequestParam(defaultValue = "name") String sortBy,
                                                                     @RequestParam(defaultValue = "ASC") String sortDirection,
                                                                     Pageable pageable) {
    Page<UserResponse> userResponsePage = userService.filterUserByNameAndGender(name, gender, sortBy, sortDirection, pageable);
    return ResponseEntity.ok(userResponsePage);
  }

  @PutMapping(path = "/update/point")
  public ResponseEntity<Integer> updateListUsers(@RequestParam(required = true) Integer point,
                                                 @RequestParam(required = true) List<UUID> userIds){
    return ResponseEntity.ok(userService.updateUserPointByListUserIds(point, userIds));
  }
}
