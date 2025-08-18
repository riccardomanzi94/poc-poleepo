package com.poleepo.usecase.checkconfig.service;

import com.poleepo.exception.GenericException;
import com.poleepo.usecase.checkconfig.model.response.CheckConfigResponseDto;
import com.poleepo.usecase.checkconfig.model.request.ConfigurationRequest;
import com.poleepo.properties.CheckConfigProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckGatewayDriver implements ICheckGatewayDriver {

    private final WebClient.Builder webClientBuilder;
    private final CheckConfigProperties checkConfigProperties;

    @Override
    public CheckConfigResponseDto callCheckConfig(@NonNull String store, @NonNull String source, @NonNull ConfigurationRequest configurationRequest) {
        log.info("Calling check config for store: {} and source: {}", store, source);

        //TODO implement check config API call with AI
        return null;
    }
}
