package com.poleepo.usecase.checkconfig.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poleepo.model.CheckConfigResponseDto;
import com.poleepo.model.request.ConfigurationRequest;
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

    private final ObjectMapper mapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    public CheckConfigResponseDto callCheckConfig(@NonNull String store, @NonNull String source, @NonNull ConfigurationRequest configurationRequest) {
        log.info("Calling check config for store: {} and source: {}", store, source);

        WebClient webClient = webClientBuilder.build();

        try {
            Mono<CheckConfigResponseDto> responseMono = webClient
                    .get()
                    .uri("http://localhost:3000/accounts/info")
                    .header("Authorization", "Bearer " + configurationRequest.getApiToken())
                    .retrieve()
                    .bodyToMono(CheckConfigResponseDto.class);

            CheckConfigResponseDto response = responseMono.block();
            log.info("Successfully received response from check config endpoint");
            return response;
        } catch (Exception e) {
            log.error("Error calling check config endpoint", e);
            return CheckConfigResponseDto.builder()
                    .active(false)
                    .shops(null)
                    .build();
        }
    }
}
