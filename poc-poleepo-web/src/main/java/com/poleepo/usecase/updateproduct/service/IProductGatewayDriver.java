package com.poleepo.usecase.updateproduct.service;

import com.poleepo.usecase.updateproduct.model.request.CreateOrUpdateProductRequest;
import com.poleepo.usecase.updateproduct.model.request.UpdateQuantityRequest;
import lombok.NonNull;

public interface IProductGatewayDriver {

    String createProduct(@NonNull String authorizationHeader,@NonNull CreateOrUpdateProductRequest createOrUpdateProductRequest);
    String updateProduct(@NonNull String authorizationHeader, @NonNull String productId,@NonNull CreateOrUpdateProductRequest createOrUpdateProductRequest);
    String updateQuantity(@NonNull String authorizationHeader, @NonNull String productId, @NonNull UpdateQuantityRequest quantityRequest);
}
