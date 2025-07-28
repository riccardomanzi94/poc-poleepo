package com.poleepo.usecase.checkconfig.service;

import com.poleepo.exception.ConfigurationAlreadyExistException;
import com.poleepo.exception.ShopNotFoundException;
import com.poleepo.properties.CheckConfigProperties;
import com.poleepo.usecase.checkconfig.model.entities.ConfigurationEntity;
import com.poleepo.usecase.checkconfig.model.request.ConfigurationRequest;
import com.poleepo.usecase.checkconfig.model.response.CheckConfigResponseDto;
import com.poleepo.usecase.checkconfig.repository.ConfigurationRepository;
import com.poleepo.usecase.retrievecategory.model.ShopDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

    @Mock
    private CheckGatewayDriver checkGatewayDriver;

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private CheckConfigProperties checkConfigProperties;

    @InjectMocks
    private ConfigurationServiceImpl configurationService;

    private ConfigurationRequest configurationRequest;
    private CheckConfigResponseDto checkConfigResponse;
    private ConfigurationEntity existingConfiguration;

    @BeforeEach
    void setUp() {
        configurationRequest = ConfigurationRequest.builder()
                .shopId("10124")
                .apiToken("01611f91d942dddb17d961d9211d7b01")
                .build();

        ShopDto validShop = ShopDto.builder()
                .id("10124")  // Modificato per corrispondere al shopId della richiesta
                .name("Test Shop")
                .status("active")
                .vatNumber("12345678901")
                .build();

        checkConfigResponse = CheckConfigResponseDto.builder()
                .active(true)
                .shops(List.of(validShop))
                .build();

        existingConfiguration = ConfigurationEntity.builder()
                .storeId(1L)
                .source(2L)
                .shopId("10124")  // Modificato per coerenza
                .apiToken("01611f91d942dddb17d961d9211d7b01")  // Modificato per coerenza
                .build();
    }

    @Test
    void createOrUpdateConfiguration_WhenConfigurationDoesNotExist_ShouldCreateSuccessfully() {
        // Given
        String store = "1";
        String source = "2";

        when(configurationRepository.findAllByStoreIdAndSource(1L, 2L))
                .thenReturn(Optional.empty());
        when(checkGatewayDriver.callCheckConfig(store, source, configurationRequest))
                .thenReturn(checkConfigResponse);
        when(configurationRepository.save(any(ConfigurationEntity.class)))
                .thenReturn(existingConfiguration);
        when(checkConfigProperties.getAvailableToken()).thenReturn("01611f91d942dddb17d961d9211d7b01,token2");

        // When
        boolean result = configurationService.createOrUpdateConfiguration(store, source, configurationRequest);

        // Then
        assertTrue(result);
        verify(configurationRepository).findAllByStoreIdAndSource(1L, 2L);
        verify(checkGatewayDriver).callCheckConfig(store, source, configurationRequest);
        verify(configurationRepository).save(any(ConfigurationEntity.class));
    }

    @Test
    void createOrUpdateConfiguration_WhenConfigurationAlreadyExists_ShouldThrowException() {
        // Given
        String store = "1";
        String source = "2";

        when(configurationRepository.findAllByStoreIdAndSource(1L, 2L))
                .thenReturn(Optional.of(existingConfiguration));
        when(checkConfigProperties.getAvailableToken()).thenReturn("01611f91d942dddb17d961d9211d7b01,token2");

        // When & Then
        ConfigurationAlreadyExistException exception = assertThrows(
                ConfigurationAlreadyExistException.class,
                () -> configurationService.createOrUpdateConfiguration(store, source, configurationRequest)
        );

        assertEquals("Configurazione già esistente per il negozio e la fonte specificati", exception.getMessage());
        verify(configurationRepository).findAllByStoreIdAndSource(1L, 2L);
        verify(checkGatewayDriver, never()).callCheckConfig(anyString(), anyString(), any());
        verify(configurationRepository, never()).save(any());
    }

    @Test
    void createOrUpdateConfiguration_WhenShopNotFoundInResponse_ShouldThrowException() {
        // Given
        String store = "1";
        String source = "2";

        ShopDto differentShop = ShopDto.builder()
                .id("shop999")  // Different shop ID
                .name("Different Shop")
                .status("active")
                .vatNumber("98765432109")
                .build();

        CheckConfigResponseDto responseWithDifferentShop = CheckConfigResponseDto.builder()
                .active(true)
                .shops(List.of(differentShop))
                .build();

        when(configurationRepository.findAllByStoreIdAndSource(1L, 2L))
                .thenReturn(Optional.empty());
        when(checkGatewayDriver.callCheckConfig(store, source, configurationRequest))
                .thenReturn(responseWithDifferentShop);
        when(checkConfigProperties.getAvailableToken()).thenReturn("01611f91d942dddb17d961d9211d7b01,token2");

        // When & Then
        ShopNotFoundException exception = assertThrows(
                ShopNotFoundException.class,
                () -> configurationService.createOrUpdateConfiguration(store, source, configurationRequest)
        );

        assertEquals("Shop non trovato nella configurazione", exception.getMessage());
        verify(configurationRepository).findAllByStoreIdAndSource(1L, 2L);
        verify(checkGatewayDriver).callCheckConfig(store, source, configurationRequest);
        verify(configurationRepository, never()).save(any());
    }

    @Test
    void createOrUpdateConfiguration_WhenShopListIsEmpty_ShouldThrowException() {
        // Given
        String store = "1";
        String source = "2";

        CheckConfigResponseDto emptyResponse = CheckConfigResponseDto.builder()
                .active(true)
                .shops(Collections.emptyList())
                .build();

        when(configurationRepository.findAllByStoreIdAndSource(1L, 2L))
                .thenReturn(Optional.empty());
        when(checkGatewayDriver.callCheckConfig(store, source, configurationRequest))
                .thenReturn(emptyResponse);
        when(checkConfigProperties.getAvailableToken()).thenReturn("01611f91d942dddb17d961d9211d7b01,token2");

        // When & Then
        ShopNotFoundException exception = assertThrows(
                ShopNotFoundException.class,
                () -> configurationService.createOrUpdateConfiguration(store, source, configurationRequest)
        );

        assertEquals("Shop non trovato nella configurazione", exception.getMessage());
    }

    @Test
    void createOrUpdateConfiguration_WhenMultipleShopsAndTargetShopExists_ShouldCreateSuccessfully() {
        // Given
        String store = "1";
        String source = "2";

        // Creo una richiesta specifica per questo test con uno shopId valido (10124)
        ConfigurationRequest specificRequest = ConfigurationRequest.builder()
                .shopId("10124")
                .apiToken("01611f91d942dddb17d961d9211d7b01")
                .build();

        ShopDto shop1 = ShopDto.builder()
                .id("shop999")
                .name("Shop 1")
                .build();

        ShopDto targetShop = ShopDto.builder()
                .id("10124")  // ID che corrisponde allo shopId della richiesta
                .name("Target Shop")
                .build();

        ShopDto shop3 = ShopDto.builder()
                .id("shop456")
                .name("Shop 3")
                .build();

        CheckConfigResponseDto multiShopResponse = CheckConfigResponseDto.builder()
                .active(true)
                .shops(List.of(shop1, targetShop, shop3))
                .build();

        when(configurationRepository.findAllByStoreIdAndSource(1L, 2L))
                .thenReturn(Optional.empty());
        when(checkGatewayDriver.callCheckConfig(store, source, specificRequest))
                .thenReturn(multiShopResponse);
        when(configurationRepository.save(any(ConfigurationEntity.class)))
                .thenReturn(existingConfiguration);
        when(checkConfigProperties.getAvailableToken()).thenReturn("01611f91d942dddb17d961d9211d7b01,token2");

        // When
        boolean result = configurationService.createOrUpdateConfiguration(store, source, specificRequest);

        // Then
        assertTrue(result);
        verify(configurationRepository).save(argThat(config ->
                config.getStoreId().equals(1L) &&
                config.getSource().equals(2L) &&
                config.getShopId().equals("10124") &&
                config.getApiToken().equals("01611f91d942dddb17d961d9211d7b01")
        ));
    }

    @Test
    void createOrUpdateConfiguration_WhenRepositorySaveFails_ShouldPropagateException() {
        // Given
        String store = "1";
        String source = "2";

        when(configurationRepository.findAllByStoreIdAndSource(1L, 2L))
                .thenReturn(Optional.empty());
        when(checkGatewayDriver.callCheckConfig(store, source, configurationRequest))
                .thenReturn(checkConfigResponse);
        when(configurationRepository.save(any(ConfigurationEntity.class)))
                .thenThrow(new RuntimeException("Database error"));
        when(checkConfigProperties.getAvailableToken()).thenReturn("01611f91d942dddb17d961d9211d7b01,token2");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                configurationService.createOrUpdateConfiguration(store, source, configurationRequest));

        assertEquals("Database error", exception.getMessage());
        verify(configurationRepository).save(any(ConfigurationEntity.class));
    }

    @Test
    void createOrUpdateConfiguration_WhenGatewayDriverFails_ShouldPropagateException() {
        // Given
        String store = "1";
        String source = "2";

        when(configurationRepository.findAllByStoreIdAndSource(1L, 2L))
                .thenReturn(Optional.empty());
        when(checkGatewayDriver.callCheckConfig(store, source, configurationRequest))
                .thenThrow(new RuntimeException("Gateway error"));
        when(checkConfigProperties.getAvailableToken()).thenReturn("01611f91d942dddb17d961d9211d7b01,token2");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                configurationService.createOrUpdateConfiguration(store, source, configurationRequest));

        assertEquals("Gateway error", exception.getMessage());
        verify(checkGatewayDriver).callCheckConfig(store, source, configurationRequest);
        verify(configurationRepository, never()).save(any());
    }

    @Test
    void createOrUpdateConfiguration_VerifyCorrectEntityCreation() {
        // Given
        String store = "123";
        String source = "456";

        when(configurationRepository.findAllByStoreIdAndSource(123L, 456L))
                .thenReturn(Optional.empty());
        when(checkGatewayDriver.callCheckConfig(store, source, configurationRequest))
                .thenReturn(checkConfigResponse);
        when(configurationRepository.save(any(ConfigurationEntity.class)))
                .thenReturn(existingConfiguration);
        when(checkConfigProperties.getAvailableToken()).thenReturn("01611f91d942dddb17d961d9211d7b01,token2");

        // When
        configurationService.createOrUpdateConfiguration(store, source, configurationRequest);

        // Then
        verify(configurationRepository).save(argThat(entity -> {
            assertEquals(123L, entity.getStoreId());
            assertEquals(456L, entity.getSource());
            assertEquals("10124", entity.getShopId());
            assertEquals("01611f91d942dddb17d961d9211d7b01", entity.getApiToken());
            return true;
        }));
    }
}
