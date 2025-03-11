package com.laundry.order.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.laundry.order.repository.FeignClient")
public class FeignClientConfig {
}
