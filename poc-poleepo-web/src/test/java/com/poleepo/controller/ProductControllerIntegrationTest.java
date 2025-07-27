package com.poleepo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poleepo.model.request.ProductRequest;
import com.poleepo.usecase.product.service.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.poleepo.config.CostantConfig.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IProductService productService;

    @Test
    void createOrUpdateProduct_success() throws Exception {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .title("Test Product")
                .categorySourceId("CAT123")
                .price(29.99)
                .vatRate(22.0)
                .quantity(10) // Campo mancante che causava errore 400
                .sourceId(null)
                .build();

        String store = "test-store";
        String source = "test-source";
        String authHeader = "Bearer token";
        String expectedResponse = "Prodotto creato con successo";

        when(productService.createOrUpdateProduct(any(ProductRequest.class), eq(source), eq(store), eq(authHeader)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(put(BASE_URI + "/products")
                        .header(X_STORE, store)
                        .header(X_SOURCE, source)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(expectedResponse));

        verify(productService, times(1)).createOrUpdateProduct(any(ProductRequest.class), eq(source), eq(store), eq(authHeader));
    }

    @Test
    void createOrUpdateProduct_withoutAuthorizationHeader() throws Exception {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .title("Test Product Without Auth")
                .categorySourceId("CAT456")
                .price(39.99)
                .vatRate(22.0)
                .quantity(5) // Campo mancante che causava errore 400
                .sourceId("123")
                .build();

        String store = "test-store";
        String source = "test-source";
        String expectedResponse = "Prodotto aggiornato";

        when(productService.createOrUpdateProduct(any(ProductRequest.class), eq(source), eq(store), isNull()))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(put(BASE_URI + "/products")
                        .header(X_STORE, store)
                        .header(X_SOURCE, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(expectedResponse));

        verify(productService, times(1)).createOrUpdateProduct(any(ProductRequest.class), eq(source), eq(store), isNull());
    }

    @Test
    void createOrUpdateProduct_missingRequiredHeaders() throws Exception {
        // Arrange
        ProductRequest request = ProductRequest.builder()
                .title("Test Product")
                .categorySourceId("CAT789")
                .price(49.99)
                .vatRate(22.0)
                .quantity(3)
                .build();

        // Act & Assert - Missing X_STORE header
        mockMvc.perform(put(BASE_URI + "/products")
                        .header(X_SOURCE, "test-source")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Act & Assert - Missing X_SOURCE header
        mockMvc.perform(put(BASE_URI + "/products")
                        .header(X_STORE, "test-store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createOrUpdateProduct(any(), any(), any(), any());
    }

    @Test
    void createOrUpdateProduct_missingRequiredFields() throws Exception {
        // Arrange - ProductRequest senza campi obbligatori
        ProductRequest invalidRequest = ProductRequest.builder()
                .title("Test Product")
                // categorySourceId, price, vatRate, quantity mancanti
                .build();

        String store = "test-store";
        String source = "test-source";

        // Act & Assert
        mockMvc.perform(put(BASE_URI + "/products")
                        .header(X_STORE, store)
                        .header(X_SOURCE, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk());

        verify(productService, never()).createOrUpdateProduct(any(), any(), any(), any());
    }

    @Test
    void createOrUpdateProduct_emptyRequestBody() throws Exception {
        // Arrange
        String store = "test-store";
        String source = "test-source";

        // Act & Assert
        mockMvc.perform(put(BASE_URI + "/products")
                        .header(X_STORE, store)
                        .header(X_SOURCE, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(productService, never()).createOrUpdateProduct(any(), any(), any(), any());
    }
}
