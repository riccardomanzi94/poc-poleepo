package com.poleepo.usecase.product.service;

import com.poleepo.model.request.CreateOrUpdateProductRequest;
import com.poleepo.model.request.ProductRequest;
import com.poleepo.properties.ProductProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private IProductGatewayDriver productGatewayDriver;

    @Mock
    private ProductProperties productProperties;

    @InjectMocks
    private ProductServiceImpl productService;

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
    void createOrUpdateProduct_createNewProduct_success() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .title("Test Product")
                .categorySourceId("CAT123")
                .price(29.99)
                .vatRate(22.0)
                .sourceId(null) // sourceId null = creazione
                .build();

        String source = "123";
        String store = "test-store";
        String authHeader = "Bearer custom-token";
        String expectedResponse = "Prodotto creato con successo";

        when(productGatewayDriver.createProduct(eq(authHeader), any(CreateOrUpdateProductRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        String result = productService.createOrUpdateProduct(request, source, store, authHeader);

        // Assert
        assertEquals(expectedResponse, result);
        verify(productGatewayDriver, times(1)).createProduct(eq(authHeader), any(CreateOrUpdateProductRequest.class));
        verify(productGatewayDriver, never()).updateProduct(anyString(), anyString(), any());
    }

    @Test
    void createOrUpdateProduct_createNewProduct_withoutAuthHeader() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .title("Test Product")
                .categorySourceId("CAT123")
                .price(29.99)
                .vatRate(22.0)
                .sourceId("") // sourceId vuoto = creazione
                .build();

        String source = "123";
        String store = "test-store";
        String defaultToken = "default-token";
        String expectedResponse = "Prodotto creato";

        when(productProperties.getDefaultToken()).thenReturn(defaultToken);
        when(productGatewayDriver.createProduct(eq("Bearer " + defaultToken), any(CreateOrUpdateProductRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        String result = productService.createOrUpdateProduct(request, source, store, null);

        // Assert
        assertEquals(expectedResponse, result);
        verify(productProperties, times(1)).getDefaultToken();
        verify(productGatewayDriver, times(1)).createProduct(eq("Bearer " + defaultToken), any(CreateOrUpdateProductRequest.class));
    }

    @Test
    void createOrUpdateProduct_updateExistingProduct_success() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .title("Updated Product")
                .categorySourceId("CAT456")
                .price(39.99)
                .vatRate(22.0)
                .sourceId("456") // sourceId presente = aggiornamento
                .build();

        String source = "123";
        String store = "test-store";
        String authHeader = "Bearer custom-token";
        String expectedResponse = "Prodotto aggiornato con successo";

        when(productGatewayDriver.updateProduct(eq(authHeader), eq("456"), any(CreateOrUpdateProductRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        String result = productService.createOrUpdateProduct(request, source, store, authHeader);

        // Assert
        assertEquals(expectedResponse, result);
        verify(productGatewayDriver, times(1)).updateProduct(eq(authHeader), eq("456"), any(CreateOrUpdateProductRequest.class));
        verify(productGatewayDriver, never()).createProduct(anyString(), any());
    }

    @Test
    void createOrUpdateProduct_updateExistingProduct_withoutAuthHeader() {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .title("Updated Product")
                .categorySourceId("CAT789")
                .price(49.99)
                .vatRate(22.0)
                .sourceId("789")
                .build();

        String source = "123";
        String store = "test-store";
        String defaultToken = "default-token";
        String expectedResponse = "Prodotto aggiornato";

        when(productProperties.getDefaultToken()).thenReturn(defaultToken);
        when(productGatewayDriver.updateProduct(eq("Bearer " + defaultToken), eq("789"), any(CreateOrUpdateProductRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        String result = productService.createOrUpdateProduct(request, source, store, null);

        // Assert
        assertEquals(expectedResponse, result);
        verify(productProperties, times(1)).getDefaultToken();
        verify(productGatewayDriver, times(1)).updateProduct(eq("Bearer " + defaultToken), eq("789"), any(CreateOrUpdateProductRequest.class));
    }
}
