package com.laundry.order.tracking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {
}
