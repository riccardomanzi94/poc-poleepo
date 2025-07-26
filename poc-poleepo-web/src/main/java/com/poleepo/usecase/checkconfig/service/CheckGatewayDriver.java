package com.poleepo.usecase.checkconfig.service;

import com.poleepo.exception.GenericException;
import com.poleepo.model.CheckConfigResponseDto;
import com.poleepo.model.request.ConfigurationRequest;
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

        final WebClient webClient = webClientBuilder.build();

        try {
            Mono<CheckConfigResponseDto> responseMono = webClient
                    .get()
                    .uri(checkConfigProperties.getUrl())
                    .header("Authorization", "Bearer " + configurationRequest.getApiToken())
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> Mono.error(new RuntimeException("Errore chiamata API: " + clientResponse.statusCode())))
                    .bodyToMono(CheckConfigResponseDto.class);

            CheckConfigResponseDto response = responseMono.block();
            log.info("Successfully received response from check config endpoint");
            return response;
        } catch (Exception e) {
            log.error("Error calling check config endpoint", e);
            throw new GenericException("Errore durante la chiamata al servizio di configurazione");
        }
    }
}
