package com.laundry.order.service.implement;

import com.laundry.order.dto.response.InventoryResponse;
import com.laundry.order.entity.Inventory;
import com.laundry.order.entity.Product;
import com.laundry.order.exception.CustomException;
import com.laundry.order.exception.ErrorCode;
import com.laundry.order.mapstruct.InventoryMapper;
import com.laundry.order.repository.InventoryRepository;
import com.laundry.order.repository.ProductRepository;
import com.laundry.order.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final ProductRepository productRepository;
  private final InventoryMapper mapper;
  @Override
  @Transactional
  public Inventory create(Product product) {
    Inventory inventory = Inventory.builder()
      .product(product)
      .reservedQuantity(0)
      .availableQuantity(product.getStockQuantity())
      .totalQuantity(product.getStockQuantity())
      .build();
    return inventoryRepository.save(inventory);
  }

  @Override
  public void checkInventory(UUID productId, int quantity) {
    Product product = productRepository.findById(productId)
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    if (product.getStockQuantity() < quantity) {
      throw new RuntimeException("Not enough stock for product: " + product.getName());
    }
  }
}
