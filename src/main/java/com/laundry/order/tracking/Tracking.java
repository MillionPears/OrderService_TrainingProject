package com.laundry.order.tracking;

import com.laundry.order.entity.Auditor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_activities")
public class Tracking extends Auditor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private UUID userId;
  private String action;
  private String endpoint;
  @Column(columnDefinition = "TEXT")
  private String requestData;
  @Column(columnDefinition = "TEXT")
  private String responseData;
  private Long duration;
}
