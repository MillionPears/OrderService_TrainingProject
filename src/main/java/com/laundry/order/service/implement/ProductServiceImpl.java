package com.laundry.order.service.implement;

import com.laundry.order.dto.request.ProductCreateRequest;
import com.laundry.order.dto.response.ProductResponse;
import com.laundry.order.entity.Product;
import com.laundry.order.exception.CustomException;
import com.laundry.order.exception.ErrorCode;
import com.laundry.order.mapstruct.ProductMapper;
import com.laundry.order.repository.ProductRepository;
import com.laundry.order.service.InventoryService;
import com.laundry.order.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper mapper;
  private final InventoryService inventoryService;

  @Override
  @Transactional
  public ProductResponse createProduct(ProductCreateRequest productCreateRequest) {
    Product product = mapper.toEntity(productCreateRequest);
    if(productRepository.existsByName(product.getName())) throw new CustomException(ErrorCode.CONFLICT,"Product Name",product.getName());
    product = productRepository.save(product);
    inventoryService.createInventory(product.getId());
    return mapper.toDTO(product);
  }

  @Override
  public ProductResponse getProductById(UUID id) {
    Product product = productRepository.findById(id).orElseThrow(
      ()-> new CustomException(ErrorCode.NOT_FOUND,"Product not found",id)
    );
    return mapper.toDTO(product);
  }

  @Override
  public List<ProductResponse> getAllUser() {
    List<Product> list = productRepository.findAll();
    return list.stream()
      .map(mapper::toDTO).toList();
  }

  @Override
  public Page<ProductResponse> filterProductByNameAndPrice(String name, BigDecimal minPrice, BigDecimal maxPrice, String sortBy, String sortDirection, Pageable pageable) {
    Sort sort =Sort.by(Sort.Direction.fromString(sortDirection),sortBy);
    Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),sort);
    Page<Product> productPage = productRepository.filterByNameAndPrice(name, minPrice, maxPrice, pageable);
    return productPage.map(mapper::toDTO);
  }


}
