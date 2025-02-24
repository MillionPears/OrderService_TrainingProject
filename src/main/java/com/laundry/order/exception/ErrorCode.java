package com.laundry.order.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request data"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),
  CONFLICT(HttpStatus.CONFLICT, "Database conflict");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
