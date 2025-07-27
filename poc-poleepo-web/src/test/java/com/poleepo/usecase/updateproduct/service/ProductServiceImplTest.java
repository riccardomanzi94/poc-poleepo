package com.poleepo.usecase.updateproduct.service;

import com.poleepo.properties.ProductProperties;
import com.poleepo.usecase.updateproduct.model.request.CreateOrUpdateProductRequest;
import com.poleepo.usecase.updateproduct.model.request.ProductRequest;
import com.poleepo.usecase.updateproduct.model.request.UpdateQuantityRequest;
import com.poleepo.usecase.updateproduct.model.response.ProductResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private IProductGatewayDriver productGatewayDriver;
    @Mock
    private ProductProperties productProperties;
    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    void createOrUpdateProduct_createsProductWhenSourceIdIsNull() {
        // Arrange
        ProductRequest productRequest = ProductRequest.builder()
                .title("Product A")
                .categorySourceId("Category1")
                .price(100.0)
                .vatRate(22.0)
                .quantity(10)
                .build();
        String source = "1";
        String store = "Store1";
        String authorizationHeader = "Bearer token";
        String expectedResponse = "12345";
        String expectedQuantity = "10";

        CreateOrUpdateProductRequest createRequest = CreateOrUpdateProductRequest.builder()
                .information(CreateOrUpdateProductRequest.Information.builder()
                        .title("Product A")
                        .category("Category1")
                        .build())
                .offer(CreateOrUpdateProductRequest.Offer.builder()
                        .price(100.0)
                        .vat(22.0)
                        .build())
                .shopId(1)
                .build();

        UpdateQuantityRequest quantityRequest = UpdateQuantityRequest.builder()
                .quantity(10)
                .shopId(1)
                .build();

        when(productGatewayDriver.createProduct(anyString(), eq(createRequest))).thenReturn(expectedResponse);
        when(productGatewayDriver.updateQuantity(anyString(), eq(expectedResponse), eq(quantityRequest))).thenReturn(expectedQuantity);

        // Act
        ProductResponse result = productServiceImpl.createOrUpdateProduct(productRequest, source, store, authorizationHeader);

        // Assert
        assertEquals("Product A", result.getTitle());
        assertEquals("Category1", result.getCategorySourceId());
        assertEquals(100.0, result.getPrice());
        assertEquals(22.0, result.getVatRate());
        assertEquals(10, result.getQuantity());
        assertEquals(1, result.getSourceId());
    }

    @Test
    void createOrUpdateProduct_updatesProductWhenSourceIdIsNotNull() {
        // Arrange
        ProductRequest productRequest = ProductRequest.builder()
                .title("Product B")
                .categorySourceId("Category2")
                .price(200.0)
                .vatRate(10.0)
                .quantity(5)
                .sourceId("2")
                .build();
        String source = "1";
        String store = "Store1";
        String authorizationHeader = null;
        String expectedResponse = "67890";
        String expectedQuantity = "5";

        CreateOrUpdateProductRequest updateRequest = CreateOrUpdateProductRequest.builder()
                .information(CreateOrUpdateProductRequest.Information.builder()
                        .title("Product B")
                        .category("Category2")
                        .build())
                .offer(CreateOrUpdateProductRequest.Offer.builder()
                        .price(200.0)
                        .vat(10.0)
                        .build())
                .shopId(2)
                .build();

        UpdateQuantityRequest quantityRequest = UpdateQuantityRequest.builder()
                .quantity(5)
                .shopId(2)
                .build();

        when(productGatewayDriver.updateProduct(anyString(), eq("2"), eq(updateRequest))).thenReturn(expectedResponse);
        when(productGatewayDriver.updateQuantity(anyString(), eq(expectedResponse), eq(quantityRequest))).thenReturn(expectedQuantity);
        when(productProperties.getAvailableToken()).thenReturn("token1,token2");

        // Act
        ProductResponse result = productServiceImpl.createOrUpdateProduct(productRequest, source, store, authorizationHeader);

        // Assert
        assertEquals("Product B", result.getTitle());
        assertEquals("Category2", result.getCategorySourceId());
        assertEquals(200.0, result.getPrice());
        assertEquals(10.0, result.getVatRate());
        assertEquals(5, result.getQuantity());
        assertEquals(2, result.getSourceId());
    }

}