package com.poleepo.usecase.checkconfig.service;

import com.poleepo.exception.ConfigurationAlreadyExistException;
import com.poleepo.exception.ConfigurationNotValidException;
import com.poleepo.exception.GenericException;
import com.poleepo.exception.ShopNotFoundException;
import com.poleepo.properties.CheckConfigProperties;
import com.poleepo.usecase.checkconfig.model.entities.ConfigurationEntity;
import com.poleepo.usecase.checkconfig.model.request.ConfigurationRequest;
import com.poleepo.usecase.checkconfig.model.response.CheckConfigResponseDto;
import com.poleepo.usecase.checkconfig.repository.ConfigurationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements IConfigurationService{

    private final ICheckGatewayDriver checkGatewayDriver;
    private final ConfigurationRepository configurationRepository;
    private final CheckConfigProperties checkConfigProperties;


    @Override
    public boolean createOrUpdateConfiguration(@NonNull String store, @NonNull String source, @NonNull ConfigurationRequest configurationRequest) {

        boolean checkApiToken = getAuthorizationHeader(configurationRequest.getApiToken(), configurationRequest.getShopId());
        if(!checkApiToken){
            throw new ConfigurationNotValidException("Token API non autorizzato per il negozio e la fonte specificati");
        }

        configurationRepository.findAllByStoreIdAndSource(Long.valueOf(store), Long.valueOf(source))
                .ifPresent(config -> {
                    log.info("Configuration already exists for store: {} and source: {}", store, source);
                    throw new ConfigurationAlreadyExistException("Configurazione già esistente per il negozio e la fonte specificati");
                });

        final CheckConfigResponseDto checkConfigResponseDto = checkGatewayDriver.callCheckConfig(store, source, configurationRequest);

        boolean verifyShop = checkConfigResponseDto.getShops().stream()
                .anyMatch(shop -> shop.getId().equals(configurationRequest.getShopId()));

        if (!verifyShop) {
            throw new ShopNotFoundException("Shop non trovato nella configurazione");
        }

        final ConfigurationEntity configurationEntity = ConfigurationEntity.builder()
                .storeId(Long.valueOf(store))
                .source(Long.valueOf(source))
                .apiToken(configurationRequest.getApiToken())
                .shopId(configurationRequest.getShopId())
                .build();
        configurationRepository.save(configurationEntity);

        return true;

    }

    private boolean getAuthorizationHeader(String apiToken, String shopId) {
        String[] splitToken = checkConfigProperties.getAvailableToken().split(",");
        switch (shopId) {
            case "10205":
                return apiToken.equals(splitToken[1]);
            case "10124":
            case "10015":
                return apiToken.equals(splitToken[0]);
            default:
                return false;
        }
    }
}
