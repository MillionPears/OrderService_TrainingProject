package com.laundry.order.repository.FeignClient;

import com.laundry.order.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name ="product-service",url ="http://localhost:9191",path = "/api/v1")
public interface ProductFeignClient {
  @GetMapping("/products/list")
  List<ProductResponse> getProductById(@RequestParam List<UUID> productIds);

//  @GetMapping("/inventories/check")
//  Boolean checkInventory(@RequestParam UUID productId, @RequestParam int quantity);

  @PostMapping("/inventories/reduce-stock")
  void checkInventoryAndReduceStock(@RequestBody Map<UUID, Integer> productQuantities);


}
