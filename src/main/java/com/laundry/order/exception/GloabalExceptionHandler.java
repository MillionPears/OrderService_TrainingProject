package com.laundry.order.exception;


import com.laundry.order.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class GloabalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
    ApiResponse<Object> response = new ApiResponse<>(
      ex.getErrorCode().getStatus().value(),
      Collections.singletonList(ex.getErrorCode().getMessage())
    );
    return ResponseEntity
      .status(ex.getStatus())
      .body(response);

  }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ApiResponse<Object> handleValidationException(MethodArgumentNotValidException ex) {
//        // Lấy tất cả các lỗi từ BindingResult
//        List<String> errorMessages = ex.getBindingResult()
//                .getAllErrors()
//                .stream()
//                .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .collect(Collectors.toList());
//
//        return new ApiResponse<Object>(
//                HttpStatus.BAD_REQUEST.value(),
//                errorMessages
//                );
//    }
//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex) {
//        return ResponseEntity
//                .status(ex.getErrorCode().getStatus())
//                .body(new ApiResponse<>(ex.getErrorCode().getStatus(), ex.getErrorCode().getMessage()));
//    }

}
