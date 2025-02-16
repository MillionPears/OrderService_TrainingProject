package com.laundry.order_svc.controller;

import com.laundry.order_svc.dto.ApiResponse;
import com.laundry.order_svc.dto.UserRequest;
import com.laundry.order_svc.dto.UserResponse;
import com.laundry.order_svc.entity.User;
import com.laundry.order_svc.enums.Gender;
import com.laundry.order_svc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Validated (UserRequest.Create.class)
            @RequestBody UserRequest userRequest){
//        System.out.println(userCreateRequest.getFullname());
        UserResponse userResponse = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(userResponse));
    }

    @PatchMapping(path = "/update/{userId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID userId,
                                                                @Validated (UserRequest.Update.class) @RequestBody UserRequest userRequest){
        UserResponse userResponse = userService.updateUser(userId,userRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(userResponse));
    }

//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUser(){
//        List<UserResponse> list = userService.getAllUsers();
//        return ResponseEntity.ok(new ApiResponse<>(list));
//    }

    @GetMapping(path = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable UUID id){
        UserResponse userResponse = userService.getUserByUserId(id);
        return ResponseEntity.ok(new ApiResponse<>(userResponse));
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchByName(
            @RequestParam String name,
            @PageableDefault(page = 0, size = 10) Pageable pageable){
        List<UserResponse> list = userService.searchUserByName(name,pageable);
        if(list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(new ApiResponse<>(list));
    }

//    @GetMapping(path = "/filter",produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<ApiResponse<List<UserResponse>>> filterByGender(
//            @RequestParam Gender gender,
//            @PageableDefault(page = 0, size = 10) Pageable pageable){
//        List<UserResponse> list = userService.filterUserByGender(gender,pageable);
//        if(list.isEmpty()) return ResponseEntity.noContent().build();
//        return ResponseEntity.ok(new ApiResponse<>(list));
//    }

    @GetMapping(path = "/filter",produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<Page<UserResponse>> searchAndFilterNotIndex(@RequestParam(required = false) String name,
                                                                       @RequestParam(required = false) Gender gender,
                                                                       @RequestParam(defaultValue = "name") String sortBy,
                                                                       @RequestParam(defaultValue = "ASC") String sortDirection,
                                                                       Pageable pageable)
    {
    Page<UserResponse> userResponsePage = userService.searchAndFilterNotIndex( name, gender,sortBy,sortDirection,pageable);
    return ResponseEntity.ok(userResponsePage);
    }

    @GetMapping(path = "/filter/index",produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<Page<UserResponse>> searchAndFilterWithIndex(@RequestParam(required = false) String name,
                                                                       @RequestParam(required = false) Gender gender,
                                                                       @RequestParam(defaultValue = "name") String sortBy,
                                                                       @RequestParam(defaultValue = "ASC") String sortDirection,
                                                                       Pageable pageable)
    {
        Page<UserResponse> userResponsePage = userService.searchAndFilterWithIndex( name, gender,sortBy,sortDirection,pageable);
        return ResponseEntity.ok(userResponsePage);
    }
}
