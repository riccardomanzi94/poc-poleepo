package com.poleepo.usecase.updateproduct.service;

import com.poleepo.exception.GenericException;
import com.poleepo.exception.ProductNotCreatedException;
import com.poleepo.exception.ProductNotUpdatedException;
import com.poleepo.usecase.updateproduct.model.request.CreateOrUpdateProductRequest;
import com.poleepo.properties.ProductProperties;
import com.poleepo.usecase.updateproduct.model.request.UpdateQuantityRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductGatewayDriver implements IProductGatewayDriver{

    private final WebClient.Builder webClientBuilder;
    private final ProductProperties productProperties;

    @Override
    public String createProduct(@NonNull String authorizationHeader, @NonNull CreateOrUpdateProductRequest createOrUpdateProductRequest) {
        log.info("Calling create products API - begin");

        try {
            String id = webClientBuilder.build()
                    .post()
                    .uri(productProperties.getUrlCreate())
                    .header("Authorization", authorizationHeader)
                    .bodyValue(createOrUpdateProductRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            clientResponse -> {
                                log.error("Error calling create products API: HTTP {}", clientResponse.statusCode());
                                throw new ProductNotCreatedException("Errore durante la chiamata al servizio di creazione dei prodotti");
                            })
                    .bodyToMono(new ParameterizedTypeReference<>() {})
                    .map(response -> {
                        return (String) ((java.util.Map<String, Object>) response).get("id");
                    })
                    .block();
            log.info("Calling create products API - end");
            return id;

        } catch (Exception e) {
            log.error("Error calling create products API: {}", e.getMessage(), e);
            throw new ProductNotCreatedException("Errore durante la chiamata al servizio dei products");
        }
    }

    @Override
    public String updateProduct(@NonNull String authorizationHeader, @NonNull String productId,@NonNull CreateOrUpdateProductRequest createOrUpdateProductRequest) {
        log.info("Calling update products API - begin");

        try {
            String id = webClientBuilder.build()
                    .put()
                    .uri(productProperties.getUrlUpdate().replace("#ID", productId))
                    .header("Authorization", authorizationHeader)
                    .bodyValue(createOrUpdateProductRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            clientResponse -> {
                                log.error("Error calling products API: HTTP {}", clientResponse.statusCode());
                                throw new ProductNotUpdatedException("Errore durante la chiamata al servizio dei products");
                            })
                    .bodyToMono(new ParameterizedTypeReference<>() {})
                    .map(response -> {
                        return (String) ((java.util.Map<String, Object>) response).get("id");
                    })
                    .block();

            log.info("Calling update products API - end");
            return id;

        } catch (Exception e) {
            log.error("Error calling update products API: {}", e.getMessage(), e);
            throw new ProductNotUpdatedException("Errore durante la chiamata al servizio dei products");
        }
    }

    @Override
    public String updateQuantity(@NonNull String authorizationHeader, @NonNull String productId, @NonNull UpdateQuantityRequest quantityRequest) {
        log.info("Calling update quantity for products API - begin");

        try {
            String id = webClientBuilder.build()
                    .put()
                    .uri(productProperties.getUrlUpdateQuantity().replace("#ID", productId))
                    .header("Authorization", authorizationHeader)
                    .bodyValue(quantityRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            clientResponse -> {
                                log.error("Error calling update quantity products API: HTTP {}", clientResponse.statusCode());
                                throw new ProductNotUpdatedException("Errore durante la chiamata al servizio aggiornamento quantity products");
                            })
                    .bodyToMono(new ParameterizedTypeReference<>() {})
                    .map(response -> {
                        return (String) ((java.util.Map<String, Object>) response).get("id");
                    })
                    .block();

            log.info("Calling update quantity products API - end");
            return id;

        } catch (Exception e) {
            log.error("Error calling update products API: {}", e.getMessage(), e);
            throw new ProductNotUpdatedException("Errore durante la chiamata al servizio aggiornamento quantity products");
        }
    }
}
