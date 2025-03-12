package com.laundry.order.kafka.consumer;


import com.laundry.order.entity.OutboxEvent;
import com.laundry.order.enums.KafkaTopic;
import com.laundry.order.enums.OutBoxEventStatus;
import com.laundry.order.parser.Parser;
import com.laundry.order.repository.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
@RequiredArgsConstructor
public class OutBoxProcessorConsumer {
  private final OutBoxRepository outboxEventRepository;

  @KafkaListener(topics = "#{T(com.laundry.order.enums.KafkaTopic).INVENTORY_REDUCE_STOCK_STATUS.getTopicName()}",
    groupId = "order-service-group")
  @Transactional
  public void consumeInventoryProcessStatusEvent(String status,@Header(KafkaHeaders.RECEIVED_KEY) String eventId){
    log.info("[OUTBOX LISTENER INVENTORY ] -- Received confirmation for eventId: {} and status : {}", eventId,status);
    OutboxEvent outboxEvent = outboxEventRepository.findById(Long.parseLong(eventId))
      .orElseThrow(() -> new RuntimeException("OutboxEvent not found"));
      outboxEvent.setStatus(OutBoxEventStatus.valueOf(status));
      outboxEventRepository.save(outboxEvent);
    log.info("[OUTBOX LISTENER INVENTORY] -- Marked event {} as status : {}", eventId,status);
  }

  @KafkaListener(topics = "#{T(com.laundry.order.enums.KafkaTopic).PAYMENT_PROCESS_STATUS.getTopicName()}",
    groupId = "order-service-group")
  @Transactional
  public void consumePaymentProcessStatusEvent(String status,@Header(KafkaHeaders.RECEIVED_KEY) String eventId){
    log.info("[OUTBOX LISTENER PAYMENT] -- Received confirmation for eventId: {} and status : {}", eventId,status);
    OutboxEvent outboxEvent = outboxEventRepository.findById(Long.parseLong(eventId))
      .orElseThrow(() -> new RuntimeException("OutboxEvent not found"));
    outboxEvent.setStatus(OutBoxEventStatus.valueOf(status));
    outboxEventRepository.save(outboxEvent);
    log.info("[OUTBOX LISTENER PAYMENT] -- Marked event {} as status : {}", eventId,status);
  }
}
