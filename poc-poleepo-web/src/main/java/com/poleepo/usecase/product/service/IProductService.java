package com.poleepo.usecase.product.service;

import com.poleepo.model.request.ProductRequest;
import lombok.NonNull;

public interface IProductService {

    String createOrUpdateProduct(@NonNull  ProductRequest productRequest, @NonNull String source, @NonNull String store,String authorizationHeader);
}
