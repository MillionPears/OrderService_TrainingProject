package com.laundry.order.service;

import com.laundry.order.dto.response.InventoryResponse;
import com.laundry.order.entity.Inventory;
import com.laundry.order.entity.Product;

import java.util.UUID;

public interface InventoryService {
  Inventory create(Product product);
//  void checkInventory(UUID productId, int quantity);
  void reduceStock(UUID productId,Integer stock);
}
