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
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Slf4j
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
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void reduceStock(UUID productId, Integer quantity) {
    Inventory inventory = inventoryRepository.findByProductId(productId)
      .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    if (inventory.getAvailableQuantity() < quantity) {
      throw new CustomException(ErrorCode.CONFLICT);
    }
    inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
    inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

    try {
      inventoryRepository.save(inventory);
      log.info("Successfully reduced stock. New values - Available: {}, Reserved: {}",
        inventory.getAvailableQuantity(), inventory.getReservedQuantity());
    } catch (OptimisticLockException e) {
      log.warn("Optimistic lock exception occurred. Will retry the operation.", e);
      throw e;
    }

  }


}
