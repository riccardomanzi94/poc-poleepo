package com.poleepo.usecase.checkconfig.service;

import com.poleepo.exception.GenericException;
import com.poleepo.model.CheckConfigResponseDto;
import com.poleepo.model.ShopDto;
import com.poleepo.model.request.ConfigurationRequest;
import com.poleepo.properties.CheckConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckGatewayDriverTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private CheckConfigProperties checkConfigProperties;

    @InjectMocks
    private CheckGatewayDriver checkGatewayDriver;

    private ConfigurationRequest configurationRequest;
    private CheckConfigResponseDto expectedResponse;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        configurationRequest = ConfigurationRequest.builder()
                .shopId("shop123")
                .apiToken("test-api-token")
                .build();

        ShopDto shopDto = ShopDto.builder()
                .id("shop123")
                .name("Test Shop")
                .status("active")
                .vatNumber("12345678901")
                .build();

        expectedResponse = CheckConfigResponseDto.builder()
                .active(true)
                .shops(List.of(shopDto))
                .build();

        // Setup basic WebClient mocking chain
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void callCheckConfig_WhenSuccessfulResponse_ShouldReturnCheckConfigResponseDto() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.just(expectedResponse));

        // When
        CheckConfigResponseDto result = checkGatewayDriver.callCheckConfig(store, source, configurationRequest);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse.isActive(), result.isActive());
        assertEquals(expectedResponse.getShops().size(), result.getShops().size());
        assertEquals("shop123", result.getShops().getFirst().getId());

        verify(webClientBuilder).build();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(testUrl);
        verify(requestHeadersSpec).header("Authorization", "Bearer test-api-token");
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(CheckConfigResponseDto.class);
    }

    @Test
    void callCheckConfig_WhenApiReturns4xxError_ShouldThrowGenericException() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.error(new WebClientResponseException(
                        "Bad Request", 400, "Bad Request", null, null, null)));

        // When & Then
        GenericException exception = assertThrows(GenericException.class, () ->
                checkGatewayDriver.callCheckConfig(store, source, configurationRequest));

        assertEquals("Errore durante la chiamata al servizio di configurazione", exception.getMessage());
        verify(webClientBuilder).build();
        verify(requestHeadersSpec).header("Authorization", "Bearer test-api-token");
    }

    @Test
    void callCheckConfig_WhenApiReturns5xxError_ShouldThrowGenericException() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.error(new WebClientResponseException(
                        "Internal Server Error", 500, "Internal Server Error", null, null, null)));

        // When & Then
        GenericException exception = assertThrows(GenericException.class, () ->
                checkGatewayDriver.callCheckConfig(store, source, configurationRequest));

        assertEquals("Errore durante la chiamata al servizio di configurazione", exception.getMessage());
    }

    @Test
    void callCheckConfig_WhenNetworkTimeout_ShouldThrowGenericException() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.error(new RuntimeException("Connection timeout")));

        // When & Then
        GenericException exception = assertThrows(GenericException.class, () ->
                checkGatewayDriver.callCheckConfig(store, source, configurationRequest));

        assertEquals("Errore durante la chiamata al servizio di configurazione", exception.getMessage());
    }

    @Test
    void callCheckConfig_WhenEmptyResponseMono_ShouldReturnNull() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.empty());

        // When
        CheckConfigResponseDto result = checkGatewayDriver.callCheckConfig(store, source, configurationRequest);

        // Then
        assertNull(result);
        verify(responseSpec).bodyToMono(CheckConfigResponseDto.class);
    }

    @Test
    void callCheckConfig_VerifyAuthorizationHeaderFormat() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";
        String customToken = "custom-bearer-token-123";

        ConfigurationRequest customRequest = ConfigurationRequest.builder()
                .shopId("shop456")
                .apiToken(customToken)
                .build();

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.just(expectedResponse));

        // When
        checkGatewayDriver.callCheckConfig(store, source, customRequest);

        // Then
        verify(requestHeadersSpec).header("Authorization", "Bearer " + customToken);
    }

    @Test
    void callCheckConfig_VerifyCorrectUrlIsUsed() {
        // Given
        String store = "store123";
        String source = "source456";
        String customUrl = "https://custom.api.endpoint.com/v2/check-config";

        when(checkConfigProperties.getUrl()).thenReturn(customUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.just(expectedResponse));

        // When
        checkGatewayDriver.callCheckConfig(store, source, configurationRequest);

        // Then
        verify(requestHeadersUriSpec).uri(customUrl);
        verify(checkConfigProperties).getUrl();
    }

    @Test
    void callCheckConfig_WhenResponseHasMultipleShops_ShouldReturnCompleteResponse() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";

        ShopDto shop1 = ShopDto.builder()
                .id("shop1")
                .name("Shop 1")
                .status("active")
                .build();

        ShopDto shop2 = ShopDto.builder()
                .id("shop2")
                .name("Shop 2")
                .status("inactive")
                .build();

        CheckConfigResponseDto multiShopResponse = CheckConfigResponseDto.builder()
                .active(false)
                .shops(List.of(shop1, shop2))
                .build();

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.just(multiShopResponse));

        // When
        CheckConfigResponseDto result = checkGatewayDriver.callCheckConfig(store, source, configurationRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isActive());
        assertEquals(2, result.getShops().size());
        assertEquals("shop1", result.getShops().getFirst().getId());
        assertEquals("shop2", result.getShops().get(1).getId());
    }

    @Test
    void callCheckConfig_WhenJsonDeserializationFails_ShouldThrowGenericException() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.error(new RuntimeException("JSON parse error")));

        // When & Then
        GenericException exception = assertThrows(GenericException.class, () ->
                checkGatewayDriver.callCheckConfig(store, source, configurationRequest));

        assertEquals("Errore durante la chiamata al servizio di configurazione", exception.getMessage());
    }

    @Test
    void callCheckConfig_VerifyWebClientBuilderInteraction() {
        // Given
        String store = "store123";
        String source = "source456";
        String testUrl = "https://api.test.com/check-config";

        when(checkConfigProperties.getUrl()).thenReturn(testUrl);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CheckConfigResponseDto.class))
                .thenReturn(Mono.just(expectedResponse));

        // When
        checkGatewayDriver.callCheckConfig(store, source, configurationRequest);

        // Then
        verify(webClientBuilder, times(1)).build();
        verifyNoMoreInteractions(webClientBuilder);
    }
}
