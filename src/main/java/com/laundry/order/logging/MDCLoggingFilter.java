package com.laundry.order.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCLoggingFilter implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    try {
      HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
      String requestId = httpRequest.getHeader("X-Request-Id");
      if (requestId == null || requestId.isEmpty()) {
        requestId = UUID.randomUUID().toString();
      }
      ThreadContext.put("requestId", requestId);
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      ThreadContext.clearAll();
    }
  }
}
