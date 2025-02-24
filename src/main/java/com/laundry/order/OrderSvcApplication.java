package com.laundry.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class OrderSvcApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderSvcApplication.class, args);
  }

}
