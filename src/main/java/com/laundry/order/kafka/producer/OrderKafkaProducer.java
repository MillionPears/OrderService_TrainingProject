package com.laundry.order.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundry.order.event.InventoryEvent;
import com.laundry.order.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {
  private final KafkaTemplate<String, String> stringKafkaTemplate;
  private final ObjectMapper objectMapper;

  private final String orderInventoryTopic = "order-inventory-topic";
  private final String orderPaymentTopic = "order-payment-topic";
  private final short replicationFactor = 2;
  private final int partitionNumber = 3;

  public void sendInventoryEvent(InventoryEvent inventoryEvent) {
    sendMessage(orderInventoryTopic, inventoryEvent);
  }

  public void sendPaymentEvent(PaymentEvent paymentEvent) {
    sendMessage(orderPaymentTopic, paymentEvent);
  }

  private void sendMessage(String topic, Object event) {
    try {
      String message = objectMapper.writeValueAsString(event);
      stringKafkaTemplate.send(topic, message);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error while serializing event: " + event.getClass().getSimpleName(), e);
    }
  }

  @Bean
  public NewTopic inventoryNewTopic() {
    return new NewTopic(orderInventoryTopic, partitionNumber, replicationFactor);
  }

  @Bean
  public NewTopic paymentNewTopic() {
    return new NewTopic(orderPaymentTopic, partitionNumber, replicationFactor);
  }
}
