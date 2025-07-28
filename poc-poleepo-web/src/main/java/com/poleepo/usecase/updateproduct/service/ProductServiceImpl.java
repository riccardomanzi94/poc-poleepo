package com.poleepo.usecase.updateproduct.service;

import com.poleepo.properties.ProductProperties;
import com.poleepo.usecase.updateproduct.model.request.CreateOrUpdateProductRequest;
import com.poleepo.usecase.updateproduct.model.request.ProductRequest;
import com.poleepo.usecase.updateproduct.model.request.UpdateQuantityRequest;
import com.poleepo.usecase.updateproduct.model.response.ProductResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl  implements IProductService{

    private static final String BEARER = "Bearer ";

    private final IProductGatewayDriver productGatewayDriver;
    private final ProductProperties productProperties;


    @Override
    public ProductResponse createOrUpdateProduct(@NonNull ProductRequest productRequest, @NonNull String source, @NonNull String store, String authorizationHeader) {

        String response;

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
            response = productGatewayDriver.createProduct(getAuthorizationHeader(authorizationHeader, source), createOrUpdateProductRequest);
        }else{
            createOrUpdateProductRequest.setShopId(Integer.valueOf(productRequest.getSourceId()));
            response = productGatewayDriver.updateProduct(getAuthorizationHeader(authorizationHeader,productRequest.getSourceId()),productRequest.getSourceId(), createOrUpdateProductRequest);
        }

        final UpdateQuantityRequest quantityRequest = UpdateQuantityRequest.builder()
                .quantity(productRequest.getQuantity())
                .shopId(createOrUpdateProductRequest.getShopId())
                .build();

        productGatewayDriver.updateQuantity(getAuthorizationHeader(authorizationHeader, String.valueOf(createOrUpdateProductRequest.getShopId())), response, quantityRequest);

        return ProductResponse.builder()
                .title(productRequest.getTitle())
                .categorySourceId(productRequest.getCategorySourceId())
                .price(productRequest.getPrice())
                .vatRate(productRequest.getVatRate())
                .quantity(productRequest.getQuantity())
                .sourceId(createOrUpdateProductRequest.getShopId())
                .build();

    }

    private String getAuthorizationHeader(String authorizationHeader,String sourceId) {
        if (authorizationHeader != null) {
            return authorizationHeader;
        }else{
            String[] splitToken = productProperties.getAvailableToken().split(",");
            if (sourceId == null) {
                return BEARER + productProperties.getDefaultToken();
            } else if (sourceId.equals("10205")) {
                return BEARER + splitToken[1];
            } else if (sourceId.equals("10124") || sourceId.equals("10015")) {
                return BEARER + splitToken[0];
            } else {
                return BEARER + productProperties.getDefaultToken();
            }
        }
    }
}
