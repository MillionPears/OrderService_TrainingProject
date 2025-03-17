package com.laundry.order.repository.FeignClient;

import com.laundry.order.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name ="product-service",url ="http://localhost:9191/" )
public interface ProductFeignClient {
  @GetMapping("/api/v1/products/list")
  List<ProductResponse> getProductById(@RequestParam List<UUID> productIds);
}
