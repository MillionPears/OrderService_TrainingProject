package com.laundry.order_svc.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private List<String> errors;
    private HttpStatus errorCode;

    private OffsetDateTime timestamp;

    public ApiResponse(HttpStatus errorCode, List<String> errors) {
        this.errorCode = errorCode;
        this.errors = errors;
        this.timestamp = OffsetDateTime.now();
    }

    public ApiResponse(T data) {
        this.data = data;
        this.timestamp = OffsetDateTime.now();
    }
}
