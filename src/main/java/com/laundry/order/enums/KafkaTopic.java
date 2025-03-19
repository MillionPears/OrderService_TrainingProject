package com.laundry.order.enums;

import lombok.Getter;

@Getter
public enum KafkaTopic {
  PROCESS_PAYMENT_EVENT("payment.process"),
  NOTIFY_PAYMENT_SUCCESS("order.success"),
  NOTIFY_INVENTORY_COMPENSATION_ACTION("inventory.compensation");


  private final String topicName;

  KafkaTopic(String topicName) {
    this.topicName = topicName;
  }
}
