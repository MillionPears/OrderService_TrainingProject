package com.laundry.order_svc.exception;


import com.laundry.order_svc.dto.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GloabalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ApiResponse<Object> handleCustomException(CustomException ex ){
        return new ApiResponse<Object>(
                ex.getErrorCode().getStatus().value(),
                Collections.singletonList(ex.getErrorCode().getMessage())
        );

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
