package com.laundry.order.repository;

import com.laundry.order.entity.OutboxEvent;
import com.laundry.order.enums.OutBoxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutBoxRepository extends JpaRepository<OutboxEvent, Long> {
  List<OutboxEvent> findByStatus(OutBoxEventStatus outBoxEventStatus);
}
