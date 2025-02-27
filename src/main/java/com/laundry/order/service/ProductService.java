package com.laundry.order.service;

import com.laundry.order.dto.request.ProductCreateRequest;
import com.laundry.order.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
  ProductResponse create (ProductCreateRequest productCreateRequest);
  ProductResponse getById(UUID id);
  List<ProductResponse> getAll();
  Page<ProductResponse> filterProductWithNameAndPrice(String name,
                                                      BigDecimal minPrice,
                                                      BigDecimal maxPrice,
                                                      String sortBy,
                                                      String sortDirection,
                                                      Pageable pageable);


}
