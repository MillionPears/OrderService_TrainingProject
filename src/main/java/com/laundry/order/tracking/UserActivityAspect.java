package com.laundry.order.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserActivityAspect {
  private final TrackingService trackingService;

  private final HttpServletRequest request;
  private final ObjectMapper objectMapper;

  @Pointcut("execution(* com.laundry.order.controller.ProductController.getById(..))")
  public void getProductById() {
  }

  @Pointcut("execution(* com.laundry.order.controller.OrderController.createOrder(..))")
  public void createOrder() {
  }

  @Pointcut("execution (* com.laundry.order.controller.UserController.searchAndFilterWithIndex(..))")
  public void searchUserByNameAndGender(){}

  @Pointcut("execution (* com.laundry.order.controller.CartController.addToCart(..))")
  public void addToCart(){}

  @Pointcut("getProductById() " +
    "|| createOrder() " +
    "|| searchUserByNameAndGender()" +
    "|| addToCart()")
  public void userAndProductActivity() {
  }

  @Around("userAndProductActivity()")
  public Object trackUserActivity(ProceedingJoinPoint joinPoint) throws Throwable {
    String userIdHeader = request.getHeader("X-User-Id");
    UUID userId = UUID.fromString(userIdHeader);
    String requestData = convertToJson(joinPoint.getArgs());
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long duration = System.currentTimeMillis() - start;
    String responseData = "";
    if (result instanceof ResponseEntity<?> responseEntity) {
      responseData = convertToJson(responseEntity.getBody());
    } else {
      responseData = convertToJson(result);
    }
    String methodName = joinPoint.getSignature().getName();
    String endpoint = request.getRequestURI();
    trackingService.recordUserActivity(userId, methodName, endpoint, requestData, responseData, duration);
    return result;
  }

  private String convertToJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("Không thể chuyển đổi sang JSON: {}", e.getMessage());
      return "Không thể chuyển đổi JSON";
    }
  }

}
