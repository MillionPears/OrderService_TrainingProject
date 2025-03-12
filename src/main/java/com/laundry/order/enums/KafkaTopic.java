package com.laundry.order.enums;

import lombok.Getter;

@Getter
public enum KafkaTopic {
  INVENTORY_REDUCE_STOCK_EVENT("inventory.update.stock"),
  PROCESS_PAYMENT_EVENT("payment.process"),

  INVENTORY_REDUCE_STOCK_STATUS("inventory.update.status"),
  PAYMENT_PROCESS_STATUS("payment.process.status");



  private final String topicName;

  KafkaTopic(String topicName) {
    this.topicName = topicName;
  }
}
