package com.laundry.order.kafka.producer.processor;

import com.laundry.order.entity.OutboxEvent;
import com.laundry.order.enums.KafkaTopic;
import com.laundry.order.enums.OutBoxEventStatus;
import com.laundry.order.repository.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class OutBoxProcessor {
  private final OutBoxRepository outboxEventRepository;
  private final KafkaTemplate<String, String> stringKafkaTemplate;
  private final String processPayment = KafkaTopic.PROCESS_PAYMENT_EVENT.getTopicName();

  @Scheduled(fixedRate = 5000)
  public void processOutboxEvents() {
    List<OutboxEvent> events = outboxEventRepository.findByStatus(OutBoxEventStatus.PENDING);
    for (OutboxEvent event : events) {
      log.info("[OUTBOX PROCESSOR] -- Status : {}", event.getStatus());
      try {
        stringKafkaTemplate.send(processPayment, String.valueOf(event.getId()), event.getPayload()).whenComplete((result, ex) -> {
          if (ex == null) {
            event.setStatus(OutBoxEventStatus.SENT);
            outboxEventRepository.save(event);
          } else {
            log.error("[OUTBOX PROCESSOR] Error sending event: {}", event.getId(), ex);
          }
        });
//        stringKafkaTemplate.send(processPayment, String.valueOf(event.getId()), event.getPayload());
          log.info("[OUTBOX PROCESSOR] -- Sent event: {}", event.getEventType());
      } catch (Exception e) {
        log.error("[OUTBOX PROCESSOR] -- Error processing event: {}", event.getId(), e);
      }
    }
  }


}
