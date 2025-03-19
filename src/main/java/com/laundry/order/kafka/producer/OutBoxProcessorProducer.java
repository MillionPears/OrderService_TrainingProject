package com.laundry.order.kafka.producer;

import com.laundry.order.entity.OutboxEvent;
import com.laundry.order.enums.KafkaTopic;
import com.laundry.order.enums.OutBoxEventStatus;
import com.laundry.order.repository.OutBoxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Log4j2
public class OutBoxProcessorProducer {

  private final String processPayment = KafkaTopic.PROCESS_PAYMENT_EVENT.getTopicName();
  private final short replicationFactor = 1;
  private final int partitionNumber = 1;

  @Bean
  public NewTopic paymentNewTopic() {
    return new NewTopic(processPayment, partitionNumber, replicationFactor);
  }
}
