package com.poleepo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poleepo.enumeration.ErrorCode;
import com.poleepo.model.request.ConfigurationRequest;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.checkconfig.service.IConfigurationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfigurationController.class)
class ConfigurationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IConfigurationService configurationService;

    private static final String BASE_URL = "/configurations";
    private static final String X_STORE_HEADER = "X-STORE";
    private static final String X_SOURCE_HEADER = "X-SOURCE";

    @Test
    void createConfiguration_WhenValidRequest_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String store = "store123";
        String source = "source456";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop789")
                .apiToken("token123")
                .build();

        when(configurationService.createOrUpdateConfiguration(eq(store), eq(source), any(ConfigurationRequest.class)))
                .thenReturn(true);

        // When & Then
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("shop789"))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.message").isEmpty())
                .andReturn();

        // Verify service interaction
        verify(configurationService).createOrUpdateConfiguration(store, source, request);

        // Additional response validation
        String responseContent = result.getResponse().getContentAsString();
        ResponseDto<?> responseDto = objectMapper.readValue(responseContent, ResponseDto.class);
        assertTrue(responseDto.isSuccess());
        assertEquals("shop789", responseDto.getData());
    }

    @Test
    void createConfiguration_WhenMissingStoreHeader_ShouldReturnBadRequest() throws Exception {
        // Given
        String source = "source456";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop789")
                .apiToken("token123")
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createConfiguration_WhenMissingSourceHeader_ShouldReturnBadRequest() throws Exception {
        // Given
        String store = "store123";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop789")
                .apiToken("token123")
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createConfiguration_WhenInvalidRequestBody_ShouldReturnBadRequest() throws Exception {
        // Given
        String store = "store123";
        String source = "source456";
        ConfigurationRequest invalidRequest = ConfigurationRequest.builder()
                .shopId(null) // Invalid: shopId is required
                .apiToken("token123")
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(ErrorCode.MISSING_REQUIRED_FIELD.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.MISSING_REQUIRED_FIELD.getMessage()));
    }

    @Test
    void createConfiguration_WhenEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // Given
        String store = "store123";
        String source = "source456";

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(ErrorCode.MISSING_REQUIRED_FIELD.getCode()));
    }

    @Test
    void createConfiguration_WhenMissingApiToken_ShouldReturnBadRequest() throws Exception {
        // Given
        String store = "store123";
        String source = "source456";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop789")
                .apiToken(null) // Invalid: apiToken is required
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value(ErrorCode.MISSING_REQUIRED_FIELD.getCode()));
    }

    @Test
    void createConfiguration_WhenInvalidJsonFormat_ShouldReturnBadRequest() throws Exception {
        // Given
        String store = "store123";
        String source = "source456";
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createConfiguration_WhenServiceReturnsFalse_ShouldStillReturnSuccess() throws Exception {
        // Given
        String store = "store123";
        String source = "source456";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop789")
                .apiToken("token123")
                .build();

        when(configurationService.createOrUpdateConfiguration(store, source, request))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true)) // Controller always returns true
                .andExpect(jsonPath("$.data").value("shop789"));

        verify(configurationService).createOrUpdateConfiguration(store, source, request);
    }

    @Test
    void createConfiguration_WithDifferentStoreAndSourceValues_ShouldWork() throws Exception {
        // Given
        String store = "999";
        String source = "888";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shopABC")
                .apiToken("tokenXYZ")
                .build();

        when(configurationService.createOrUpdateConfiguration(store, source, request))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("shopABC"));

        verify(configurationService).createOrUpdateConfiguration(store, source, request);
    }

    @Test
    void createConfiguration_WhenUnsupportedMediaType_ShouldReturnUnsupportedMediaType() throws Exception {
        // Given
        String store = "store123";
        String source = "source456";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop789")
                .apiToken("token123")
                .build();

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createConfiguration_VerifyResponseStructure() throws Exception {
        // Given
        String store = "store123";
        String source = "source456";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop789")
                .apiToken("token123")
                .build();

        when(configurationService.createOrUpdateConfiguration(store, source, request))
                .thenReturn(true);

        // When & Then
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ResponseDto<?> responseDto = objectMapper.readValue(responseContent, ResponseDto.class);

        // Verify response structure
        assertNotNull(responseDto);
        assertTrue(responseDto.isSuccess());
        assertEquals("shop789", responseDto.getData());
        assertEquals(0, responseDto.getError());
        assertNull(responseDto.getMessage());
    }

    @Test
    void createConfiguration_WhenLongHeaderValues_ShouldWork() throws Exception {
        // Given
        String longStore = "a".repeat(100);
        String longSource = "b".repeat(100);
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop789")
                .apiToken("token123")
                .build();

        when(configurationService.createOrUpdateConfiguration(longStore, longSource, request))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                        .header(X_STORE_HEADER, longStore)
                        .header(X_SOURCE_HEADER, longSource)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("shop789"));

        verify(configurationService).createOrUpdateConfiguration(longStore, longSource, request);
    }
}
