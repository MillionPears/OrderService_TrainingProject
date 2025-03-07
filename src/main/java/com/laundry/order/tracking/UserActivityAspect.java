package com.laundry.order.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class UserActivityAspect {
  private final TrackingService trackingService;

  private final HttpServletRequest request;
  private final ObjectMapper objectMapper;

  @Pointcut("execution(* com.laundry.order.controller.ProductController.getProductById(..))")
  public void getProductById() {
  }

  @Pointcut("execution (* com.laundry.order.controller.CartController.addToCart(..))")
  public void addToCart() {
  }

  @Pointcut("execution(* com.laundry.order.controller.OrderController.createOrder(..))")
  public void createOrder() {
  }

  @Pointcut("getProductById() " +
    "|| addToCart()" +
    "|| createOrder()")
  public void userAndProductActivity() {
  }

  @Around("userAndProductActivity()")
  public Object trackUserActivity(ProceedingJoinPoint joinPoint) throws Throwable {
    UUID userId = null;
    String requestData = "";
    long start = System.currentTimeMillis();
    String responseData = "";
    String methodName = joinPoint.getSignature().getName();
    String endpoint = request.getRequestURI();

    try {
      String userIdHeader = request.getHeader("X-User-Id");
      userId = UUID.fromString(userIdHeader);
      requestData = convertToJson(joinPoint.getArgs());
    } catch (Exception e) {
      log.error("[USER TRACKING] - Lỗi tracking trước khi gọi proceed: {}", e.getMessage());
    }

    Object result;
    try {
      result = joinPoint.proceed();
      if (result instanceof ResponseEntity<?> responseEntity) {
        responseData = convertToJson(responseEntity.getBody());
      }
    } catch (Throwable ex) {
      responseData = "Request failed with exception: " + ex.getMessage();
      log.error("Request bị lỗi trong proceed: {}", ex.getMessage(), ex);
      throw ex;
    } finally {
      if (userId != null) {
        long duration = System.currentTimeMillis() - start;
        try {
          trackingService.recordUserActivity(
            userId, methodName, endpoint, requestData, responseData, duration
          );
          log.info("[USER TRACKING] - Tracking Successfully: {} - {}", methodName, endpoint);
        } catch (Exception e) {
          log.error("Tracking Error while record activity: {}", e.getMessage());
        }
      } else {
        log.warn("[USER TRACKING] - Không thể tracking vì userId không hợp lệ hoặc lỗi tracking trước proceed");
      }
    }

    return result;
  }


  private String convertToJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("[USER TRACKING] - Do not convert to JSON: [{}]", e.getMessage());
      return "Do not convert to JSON";
    }
  }

}
