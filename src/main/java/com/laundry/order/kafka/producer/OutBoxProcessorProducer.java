package com.laundry.order.kafka.producer;

import com.laundry.order.enums.KafkaTopic;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutBoxProcessorProducer {
  private final KafkaTemplate<String, String> stringKafkaTemplate;

  private final String reduceInventory = KafkaTopic.INVENTORY_REDUCE_STOCK_EVENT.getTopicName();
  private final String processPayment = KafkaTopic.PROCESS_PAYMENT_EVENT.getTopicName();
  private final short replicationFactor = 1;
  private final int partitionNumber = 3;

  public void sendInventoryEvent(String key,String inventoryEvent) {
    stringKafkaTemplate.send(reduceInventory,key,inventoryEvent);
  }

  public void sendPaymentEvent(String key,String paymentEvent) {
    stringKafkaTemplate.send(processPayment,key,paymentEvent);
  }


  @Bean
  public NewTopic inventoryNewTopic() {
    return new NewTopic(reduceInventory, partitionNumber, replicationFactor);
  }

  @Bean
  public NewTopic paymentNewTopic() {
    return new NewTopic(processPayment, partitionNumber, replicationFactor);
  }
}
