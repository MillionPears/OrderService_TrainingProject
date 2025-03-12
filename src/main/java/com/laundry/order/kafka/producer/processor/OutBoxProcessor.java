package com.laundry.order.kafka.producer.processor;

import com.laundry.order.entity.OutboxEvent;
import com.laundry.order.enums.OutBoxEventStatus;
import com.laundry.order.kafka.producer.OutBoxProcessorProducer;
import com.laundry.order.repository.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class OutBoxProcessor {
  private final OutBoxRepository outboxEventRepository;
  private final OutBoxProcessorProducer orderKafkaProducer;

  @Scheduled(fixedRate = 5000)
  @Transactional
  public void processOutboxEvents() {
    List<OutboxEvent> events = outboxEventRepository.findByStatus(OutBoxEventStatus.PENDING);
    for (OutboxEvent event : events) {
      event.setStatus(OutBoxEventStatus.PROCESSING);
      outboxEventRepository.save(event);
      log.info("[OUTBOX PROCESSOR] -- Status : {}", event.getStatus());
      try {
        if ("INVENTORY_EVENT".equals(event.getEventType())) {
          orderKafkaProducer.sendInventoryEvent(String.valueOf(event.getId()), event.getPayload());
        } else if ("PAYMENT_EVENT".equals(event.getEventType())) {
          orderKafkaProducer.sendPaymentEvent(String.valueOf(event.getId()), event.getPayload());
        }
        log.info("[OUTBOX PROCESSOR] -- Sent event: {}", event.getEventType());
      } catch (Exception e) {
        log.error("[OUTBOX PROCESSOR] -- Error processing event: {}", event.getId(), e);
      }
    }
  }
}
