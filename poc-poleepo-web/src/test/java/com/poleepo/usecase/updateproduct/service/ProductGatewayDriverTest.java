package com.poleepo.usecase.updateproduct.service;

import com.poleepo.exception.ProductNotCreatedException;
import com.poleepo.exception.ProductNotUpdatedException;
import com.poleepo.properties.ProductProperties;
import com.poleepo.usecase.updateproduct.model.request.CreateOrUpdateProductRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductGatewayDriverTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ProductProperties productProperties;

    @InjectMocks
    private ProductGatewayDriver productGatewayDriver;

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
    void createProduct_success() {
        // Arrange
        String authHeader = "Bearer token";
        CreateOrUpdateProductRequest request = CreateOrUpdateProductRequest.builder().build();
        String createUrl = "http://api.test/products";
        String expectedId = "12345";
        Map<String, Object> responseMap = Map.of("id", expectedId);

        when(productProperties.getUrlCreate()).thenReturn(createUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(createUrl)).thenReturn(requestBodySpec);
        when(requestBodySpec.header("Authorization", authHeader)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(responseMap));

        // Act
        String result = productGatewayDriver.createProduct(authHeader, request);

        // Assert
        assertEquals(expectedId, result);
        verify(productProperties, times(1)).getUrlCreate();
        verify(webClientBuilder, times(1)).build();
        verify(webClient, times(1)).post();
    }

    @Test
    void createProduct_genericError_throwsGenericException() {
        // Arrange
        String authHeader = "Bearer token";
        CreateOrUpdateProductRequest request = CreateOrUpdateProductRequest.builder().build();
        String createUrl = "http://api.test/products";

        when(productProperties.getUrlCreate()).thenReturn(createUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(createUrl)).thenReturn(requestBodySpec);
        when(requestBodySpec.header("Authorization", authHeader)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        ProductNotCreatedException exception = assertThrows(ProductNotCreatedException.class,
            () -> productGatewayDriver.createProduct(authHeader, request));

        assertEquals("Errore durante la chiamata al servizio dei products", exception.getMessage());
    }

    @Test
    void updateProduct_success() {
        // Arrange
        String authHeader = "Bearer token";
        String productId = "123";
        CreateOrUpdateProductRequest request = CreateOrUpdateProductRequest.builder().build();
        String updateUrlTemplate = "http://api.test/products/#ID";
        String expectedUpdateUrl = "http://api.test/products/123";
        String expectedId = "123";
        Map<String, Object> responseMap = Map.of("id", expectedId);

        when(productProperties.getUrlUpdate()).thenReturn(updateUrlTemplate);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(expectedUpdateUrl)).thenReturn(requestBodySpec);
        when(requestBodySpec.header("Authorization", authHeader)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(responseMap));

        // Act
        String result = productGatewayDriver.updateProduct(authHeader, productId, request);

        // Assert
        assertEquals(expectedId, result);
        verify(productProperties, times(1)).getUrlUpdate();
        verify(webClientBuilder, times(1)).build();
        verify(webClient, times(1)).put();
        verify(requestBodyUriSpec, times(1)).uri(expectedUpdateUrl);
    }

    @Test
    void updateProduct_genericError_throwsGenericException() {
        // Arrange
        String authHeader = "Bearer token";
        String productId = "123";
        CreateOrUpdateProductRequest request = CreateOrUpdateProductRequest.builder().build();
        String updateUrlTemplate = "http://api.test/products/#ID";
        String expectedUpdateUrl = "http://api.test/products/123";

        when(productProperties.getUrlUpdate()).thenReturn(updateUrlTemplate);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(expectedUpdateUrl)).thenReturn(requestBodySpec);
        when(requestBodySpec.header("Authorization", authHeader)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenThrow(new RuntimeException("Connection timeout"));

        // Act & Assert
        ProductNotUpdatedException exception = assertThrows(ProductNotUpdatedException.class,
            () -> productGatewayDriver.updateProduct(authHeader, productId, request));

        assertEquals("Errore durante la chiamata al servizio dei products", exception.getMessage());
    }

    @Test
    void updateProduct_urlReplacement() {
        // Arrange
        String authHeader = "Bearer token";
        String productId = "456";
        CreateOrUpdateProductRequest request = CreateOrUpdateProductRequest.builder().build();
        String updateUrlTemplate = "http://api.test/products/#ID/update";
        String expectedUpdateUrl = "http://api.test/products/456/update";
        String expectedId = "456";
        Map<String, Object> responseMap = Map.of("id", expectedId);

        when(productProperties.getUrlUpdate()).thenReturn(updateUrlTemplate);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(expectedUpdateUrl)).thenReturn(requestBodySpec);
        when(requestBodySpec.header("Authorization", authHeader)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(request)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(responseMap));

        // Act
        String result = productGatewayDriver.updateProduct(authHeader, productId, request);

        // Assert
        assertEquals(expectedId, result);
        verify(requestBodyUriSpec, times(1)).uri(expectedUpdateUrl);
    }
}
