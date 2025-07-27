package com.poleepo.usecase.product.service;

import com.poleepo.model.request.CreateOrUpdateProductRequest;
import lombok.NonNull;

public interface IProductGatewayDriver {

    String createProduct(@NonNull String authorizationHeader,@NonNull CreateOrUpdateProductRequest createOrUpdateProductRequest);
    String updateProduct(@NonNull String authorizationHeader, @NonNull String productId,@NonNull CreateOrUpdateProductRequest createOrUpdateProductRequest);
}
