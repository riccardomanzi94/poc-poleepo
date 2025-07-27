package com.poleepo.usecase.retrievecategory.service;

import com.poleepo.exception.GenericException;
import com.poleepo.usecase.retrievecategory.model.response.CategoryResponse;
import com.poleepo.properties.CategoryProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryGatewayDriver implements ICategoryGatewayDriver {

    private final WebClient.Builder webClientBuilder;
    private final CategoryProperties categoryProperties;

    @Override
    public List<CategoryResponse> getCategories(String authorizationHeader) {
        log.info("Calling categories API - begin");

        try {
            List<CategoryResponse> categories = webClientBuilder.build()
                    .get()
                    .uri(categoryProperties.getUrl())
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                log.error("Error calling categories API: HTTP {}", clientResponse.statusCode());
                                throw new GenericException("Errore durante la chiamata al servizio delle categorie");
                            })
                    .bodyToMono(new ParameterizedTypeReference<List<CategoryResponse>>() {})
                    .block();

            log.info("Successfully retrieved {} categories", categories != null ? categories.size() : 0);
            log.info("Calling categories API - end");
            return categories;

        } catch (Exception e) {
            log.error("Error calling categories API: {}", e.getMessage(), e);
            throw new GenericException("Errore durante la chiamata al servizio delle categorie");
        }
    }
}
