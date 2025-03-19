package com.laundry.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // Order Service (10)
  ORDER_NOT_FOUND(10001, HttpStatus.NOT_FOUND, "Order with ID {} not found"),
  PRODUCT_OUT_OF_STOCK(10002, HttpStatus.NOT_FOUND, "Product with ID {} has been sold out"),
  USER_NOT_FOUND(10003, HttpStatus.NOT_FOUND, "User with ID {} not found"),
  CHECK_INVENTORY_FAIL(10004, HttpStatus.CONFLICT, "Check Inventory failed"),
  PHONE_NUMBER_ALREADY_EXISTS(10005, HttpStatus.CONFLICT, "PhoneNumber: {} already exists"),
  INVALID_FIELD(10006, HttpStatus.BAD_REQUEST, "Invalid field"),
  OPTIMISTIC_FAILURE(10007,HttpStatus.CONFLICT,"Optimistic Lock");


  private final int code;
  private final HttpStatus status;
  private final String messageTemplate;

  ErrorCode(int code, HttpStatus status, String messageTemplate) {
    this.code=code;
    this.status = status;
    this.messageTemplate = messageTemplate;
  }

  public String formatMessage(Object... args) {
    return String.format(messageTemplate.replace("{}", "%s"), args);
  }
}

