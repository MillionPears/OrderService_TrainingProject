package com.laundry.order.repository;

import com.laundry.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


public interface OrderRepository extends JpaRepository<Order, Long> {
  Optional<Order> findByIdempotentKey(UUID idempotencyKey);
  boolean existsByIdempotentKey(UUID idempotentKey);
}
