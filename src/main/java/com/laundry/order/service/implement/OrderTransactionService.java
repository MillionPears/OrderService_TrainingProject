package com.laundry.order.service.implement;

import com.laundry.order.entity.Order;
import com.laundry.order.entity.Product;
import com.laundry.order.exception.CustomException;
import com.laundry.order.exception.ErrorCode;
import com.laundry.order.repository.OrderRepository;
import com.laundry.order.repository.ProductRepository;
import com.laundry.order.service.InventoryService;
import com.laundry.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderTransactionService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;

  @Transactional()
  public Order saveOrder(Order order) {
    return orderRepository.save(order);
  }

//  @Transactional()
//  public void reduceProductStock(UUID productId, int quantity){
//    Product product = productRepository.findById(productId).orElseThrow(
//      ()-> new CustomException(ErrorCode.NOT_FOUND));
//    product.setStockQuantity(product.getStockQuantity() - quantity);
//    productRepository.save(product);
//  }
}
