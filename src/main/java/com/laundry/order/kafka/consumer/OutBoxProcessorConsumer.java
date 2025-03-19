package com.laundry.order.kafka.consumer;


import com.laundry.order.entity.Order;
import com.laundry.order.entity.OutboxEvent;
import com.laundry.order.enums.KafkaTopic;
import com.laundry.order.enums.OrderStatus;
import com.laundry.order.enums.OutBoxEventStatus;
import com.laundry.order.event.PaymentEvent;
import com.laundry.order.parser.Parser;
import com.laundry.order.repository.OrderRepository;
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
  private final OrderRepository orderRepository;
  private final Parser parser;

  @KafkaListener(topics = "#{T(com.laundry.order.enums.KafkaTopic).NOTIFY_PAYMENT_SUCCESS.getTopicName()}",
    groupId = "order-service-group")
  public void consumePaymentProcessStatusEvent(String receivedEvent){
    PaymentEvent paymentEvent = parser.parseToObject(receivedEvent, PaymentEvent.class);
    Order order = orderRepository.findById(paymentEvent.getOrderId())
      .orElseThrow(() -> new RuntimeException("Order not found"));
    order.setStatus(OrderStatus.COMPLETED);
    orderRepository.save(order);
  }

  @KafkaListener(topics = "#{T(com.laundry.order.enums.KafkaTopic).NOTIFY_INVENTORY_COMPENSATION_ACTION.getTopicName()}",
    groupId = "order-service-group")
  public void consumeInventoryCompensationEvent(String receivedEvent){
    PaymentEvent paymentEvent = parser.parseToObject(receivedEvent, PaymentEvent.class);
    Order order = orderRepository.findById(paymentEvent.getOrderId())
      .orElseThrow(() -> new RuntimeException("Order not found"));
    order.setStatus(OrderStatus.CANCELED);
    orderRepository.save(order);
  }
}
