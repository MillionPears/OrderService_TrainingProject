package com.laundry.order.tracking;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserActivityAspect {
  private final TrackingService trackingService;

  @PostConstruct
  public void init() {   // check bean created?
    log.info("UserActivityAspect initialized");
  }

  @Pointcut("execution(* com.laundry.order.controller.ProductController.getById(..))")
  public void getProductById() {
  }

  @Pointcut("execution(* com.laundry.order.controller.OrderController.createOrder(..))")
  public void createOrder() {
  }

  @Before("getProductById()")
  public void beforeGetById() {
    log.info("Before getById execution");
  }

  @Pointcut("getProductById() || createOrder()")
  public void userAndProductActivity() {
  }

  @Around("userAndProductActivity()")
  public Object trackUserActivity(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      log.warn("Không thể lấy request.");
      return joinPoint.proceed();
    }
    HttpServletRequest request = attributes.getRequest();
    String userIdHeader = request.getHeader("X-User-Id");

    if (userIdHeader == null) {
      log.warn("Header 'X-User-Id' không được cung cấp.");
      return joinPoint.proceed();
    }
    UUID userId;
    try {
      userId = UUID.fromString(userIdHeader);
    } catch (IllegalArgumentException e) {
      log.error("UserId không hợp lệ: {}", userIdHeader);
      return joinPoint.proceed();
    }

    String methodName = joinPoint.getSignature().getName();
    String endpoint = request.getRequestURI();
    String requestData = joinPoint.getArgs() != null ? Arrays.toString(joinPoint.getArgs()) : "No Request Data";
    Object result = joinPoint.proceed();
    long duration = System.currentTimeMillis() - start;

    String responseData;
    if (result instanceof ResponseEntity<?> responseEntity) {
      Object body = responseEntity.getBody();
      responseData = body != null ? body.toString() : "No Response Data";
    } else {
      responseData = result != null ? result.toString() : "No Response Data";
    }
    trackingService.recordUserActivity(userId, methodName, endpoint, requestData, responseData, duration);
    return result;
  }

}
