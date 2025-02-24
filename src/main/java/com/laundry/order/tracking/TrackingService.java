package com.laundry.order.tracking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingService {
  private final TrackingRepository trackingRepository;

  public void recordUserActivity(UUID userId, String action, String endpoint, String requestData, String responseData, Long duration) {
    Tracking tracking = new Tracking();
    tracking.setUserId(userId);
    tracking.setAction(action);
    tracking.setEndpoint(endpoint);
    tracking.setRequestData(requestData);
    tracking.setResponseData(responseData);
    tracking.setDuration(duration);
    trackingRepository.save(tracking);
    System.out.println("Tracking saved successfully for user: " + userId);

  }


}
