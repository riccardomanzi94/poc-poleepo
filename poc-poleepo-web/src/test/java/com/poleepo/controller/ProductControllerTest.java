package com.poleepo.controller;

import com.poleepo.usecase.updateproduct.model.request.ProductRequest;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.updateproduct.model.response.ProductResponse;
import com.poleepo.usecase.updateproduct.service.IProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private IProductService productService;

    @InjectMocks
    private ProductController productController;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createOrUpdateProduct_success() {
        // Arrange
        String store = "test-store";
        String source = "test-source";
        String authorizationHeader = "Bearer token";
        ProductRequest request = new ProductRequest();
        ProductResponse expectedResponse = ProductResponse.builder()
                .title("Test Product")
                .categorySourceId("CAT123")
                .price(29.99)
                .vatRate(22.0)
                .quantity(10)
                .sourceId(123)
                .build();

        when(productService.createOrUpdateProduct(any(ProductRequest.class), eq(source), eq(store), eq(authorizationHeader)))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ResponseDto<ProductResponse>> response = productController.createOrUpdateProduct(
                store, source, request, authorizationHeader);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(expectedResponse, response.getBody().getData());
        verify(productService, times(1)).createOrUpdateProduct(request, source, store, authorizationHeader);
    }

    @Test
    void createOrUpdateProduct_withoutAuthorizationHeader() {
        // Arrange
        String store = "test-store";
        String source = "test-source";
        ProductRequest request = new ProductRequest();
        ProductResponse expectedResponse = ProductResponse.builder()
                .title("Test Product")
                .categorySourceId("CAT123")
                .price(29.99)
                .vatRate(22.0)
                .quantity(10)
                .sourceId(123)
                .build();

        when(productService.createOrUpdateProduct(any(ProductRequest.class), eq(source), eq(store), isNull()))
                .thenReturn(expectedResponse);

        // Act
        ResponseEntity<ResponseDto<ProductResponse>> response = productController.createOrUpdateProduct(
                store, source, request, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(expectedResponse, response.getBody().getData());
        verify(productService, times(1)).createOrUpdateProduct(request, source, store, null);
    }
}
