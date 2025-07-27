package com.poleepo.usecase.updateproduct.service;

import com.poleepo.usecase.updateproduct.model.request.ProductRequest;
import com.poleepo.usecase.updateproduct.model.response.ProductResponse;
import lombok.NonNull;

public interface IProductService {

    ProductResponse createOrUpdateProduct(@NonNull  ProductRequest productRequest, @NonNull String source, @NonNull String store, String authorizationHeader);
}
