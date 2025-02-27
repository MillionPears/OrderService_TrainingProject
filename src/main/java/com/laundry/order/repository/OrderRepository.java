package com.laundry.order.repository;

import com.laundry.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
  Optional<Order> findByIdempotentKey(UUID idempotencyKey);
}
