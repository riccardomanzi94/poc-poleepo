package com.poleepo.controller;

import com.poleepo.usecase.checkconfig.model.request.ConfigurationRequest;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.checkconfig.service.ConfigurationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationControllerTest {

    @Mock
    private ConfigurationServiceImpl configurationService;

    @InjectMocks
    private ConfigurationController configurationController;

    @Test
    void createConfigurationReturnsSuccessWhenValidRequest() {
        String store = "store123";
        String source = "sourceABC";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop456")
                .apiToken("token123")
                .build();

        when(configurationService.createOrUpdateConfiguration(store, source, request)).thenReturn(true);

        ResponseEntity<ResponseDto<String>> response = configurationController.createConfiguration(store, source, request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("shop456", response.getBody().getData());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configurationService).createOrUpdateConfiguration(store, source, request);
    }

    @Test
    void createConfigurationReturnsDifferentShopId() {
        String store = "store789";
        String source = "sourceXYZ";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shop999")
                .apiToken("token456")
                .build();

        when(configurationService.createOrUpdateConfiguration(store, source, request)).thenReturn(true);

        ResponseEntity<ResponseDto<String>> response = configurationController.createConfiguration(store, source, request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("shop999", response.getBody().getData());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configurationService).createOrUpdateConfiguration(store, source, request);
    }

    @Test
    void createConfigurationAlwaysReturnsSuccessTrue() {
        String store = "storeTest";
        String source = "sourceTest";
        ConfigurationRequest request = ConfigurationRequest.builder()
                .shopId("shopTest")
                .apiToken("tokenTest")
                .build();

        when(configurationService.createOrUpdateConfiguration(store, source, request)).thenReturn(false);

        ResponseEntity<ResponseDto<String>> response = configurationController.createConfiguration(store, source, request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess()); // Il controller hardcoded success=true
        assertEquals("shopTest", response.getBody().getData());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(configurationService).createOrUpdateConfiguration(store, source, request);
    }
}