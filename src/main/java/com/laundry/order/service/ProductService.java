package com.laundry.order.service;

import com.laundry.order.dto.request.ProductCreateRequest;
import com.laundry.order.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
  ProductResponse create (ProductCreateRequest productCreateRequest);
  ProductResponse getById(UUID id);
  List<ProductResponse> getAll();

}
