package com.laundry.order_svc.exception;


import com.laundry.order_svc.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Collections;

@RestControllerAdvice
public class GloabalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ApiResponse<Object> handleCustomException(CustomException ex ){
        return new ApiResponse<Object>(
                ex.getErrorCode().getStatus().value(),
                Collections.singletonList(ex.getErrorCode().getMessage())
        );

    }

//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException ex) {
//        return ResponseEntity
//                .status(ex.getErrorCode().getStatus())
//                .body(new ApiResponse<>(ex.getErrorCode().getStatus(), ex.getErrorCode().getMessage()));
//    }

}
