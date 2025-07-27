package com.poleepo.usecase.product.service;

import com.poleepo.model.request.CreateOrUpdateProductRequest;
import com.poleepo.model.request.ProductRequest;
import com.poleepo.properties.ProductProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl  implements IProductService{

    private final IProductGatewayDriver productGatewayDriver;
    private final ProductProperties productProperties;


    @Override
    public String createOrUpdateProduct(@NonNull ProductRequest productRequest, @NonNull String source, @NonNull String store,String authorizationHeader) {

        final CreateOrUpdateProductRequest createOrUpdateProductRequest = CreateOrUpdateProductRequest.builder()
                .information(CreateOrUpdateProductRequest.Information.builder()
                        .title(productRequest.getTitle())
                        .category(productRequest.getCategorySourceId())
                        .build())
                .offer(CreateOrUpdateProductRequest.Offer.builder()
                        .price(productRequest.getPrice())
                        .vat(productRequest.getVatRate())
                        .build())
                .build();

        if(productRequest.getSourceId() == null || productRequest.getSourceId().isEmpty()){
            createOrUpdateProductRequest.setShopId(Integer.valueOf(source));
            return productGatewayDriver.createProduct(authorizationHeader != null ? authorizationHeader : "Bearer " + productProperties.getDefaultToken(), createOrUpdateProductRequest);
        }else{
            createOrUpdateProductRequest.setShopId(Integer.valueOf(productRequest.getSourceId()));
            return productGatewayDriver.updateProduct(authorizationHeader != null ? authorizationHeader : "Bearer " + productProperties.getDefaultToken(),productRequest.getSourceId(), createOrUpdateProductRequest);
        }
    }
}
