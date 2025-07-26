package com.poleepo.usecase.retrievecategory.service;

import com.poleepo.exception.GenericException;
import com.poleepo.model.response.CategoryResponse;
import com.poleepo.properties.CategoryProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryGatewayDriverTest {

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
    private CategoryProperties categoryProperties;

    @InjectMocks
    private CategoryGatewayDriver categoryGatewayDriver;

    @Test
    void getCategories_WhenSuccessfulResponse_ShouldReturnCategories() {
        // Given
        String authorizationHeader = "Bearer token123";
        String apiUrl = "https://api.example.com/categories";

        List<CategoryResponse> expectedCategories = Arrays.asList(
                CategoryResponse.builder()
                        .id("cat1")
                        .name("Electronics")
                        .children(Collections.emptyList())
                        .build(),
                CategoryResponse.builder()
                        .id("cat2")
                        .name("Clothing")
                        .children(Collections.emptyList())
                        .build()
        );

        when(categoryProperties.getUrl()).thenReturn(apiUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(apiUrl)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Authorization", authorizationHeader)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedCategories));

        // When
        List<CategoryResponse> result = categoryGatewayDriver.getCategories(authorizationHeader);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("cat1", result.get(0).getId());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("cat2", result.get(1).getId());
        assertEquals("Clothing", result.get(1).getName());

        verify(categoryProperties).getUrl();
        verify(webClientBuilder).build();
        verify(webClient).get();
        verify(requestHeadersUriSpec).uri(apiUrl);
        verify(requestHeadersSpec).header("Authorization", authorizationHeader);
        verify(requestHeadersSpec).retrieve();
    }

    @Test
    void getCategories_WhenEmptyResponse_ShouldReturnEmptyList() {
        // Given
        String authorizationHeader = "Bearer token456";
        String apiUrl = "https://api.example.com/categories";

        List<CategoryResponse> emptyCategories = Collections.emptyList();

        when(categoryProperties.getUrl()).thenReturn(apiUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(apiUrl)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Authorization", authorizationHeader)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(emptyCategories));

        // When
        List<CategoryResponse> result = categoryGatewayDriver.getCategories(authorizationHeader);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryProperties).getUrl();
        verify(webClientBuilder).build();
    }

    @Test
    void getCategories_When4xxError_ShouldThrowGenericException() {
        // Given
        String authorizationHeader = "Bearer invalidToken";
        String apiUrl = "https://api.example.com/categories";

        when(categoryProperties.getUrl()).thenReturn(apiUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(apiUrl)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Authorization", authorizationHeader)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(new WebClientResponseException(HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized", null, null, null)));

        // When & Then
        GenericException exception = assertThrows(GenericException.class,
                () -> categoryGatewayDriver.getCategories(authorizationHeader));

        assertEquals("Errore durante la chiamata al servizio delle categorie", exception.getMessage());

        verify(categoryProperties).getUrl();
        verify(webClientBuilder).build();
    }

    @Test
    void getCategories_WhenRuntimeException_ShouldThrowGenericException() {
        // Given
        String authorizationHeader = "Bearer token123";
        String apiUrl = "https://api.example.com/categories";

        when(categoryProperties.getUrl()).thenReturn(apiUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(apiUrl)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Authorization", authorizationHeader)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(new RuntimeException("Connection timeout")));

        // When & Then
        GenericException exception = assertThrows(GenericException.class,
                () -> categoryGatewayDriver.getCategories(authorizationHeader));

        assertEquals("Errore durante la chiamata al servizio delle categorie", exception.getMessage());

        verify(categoryProperties).getUrl();
        verify(webClientBuilder).build();
    }

    @Test
    void getCategories_WhenSingleCategory_ShouldReturnSingleCategory() {
        // Given
        String authorizationHeader = "Bearer singleToken";
        String apiUrl = "https://api.example.com/categories";

        List<CategoryResponse> singleCategory = Collections.singletonList(
                CategoryResponse.builder()
                        .id("single1")
                        .name("Single Category")
                        .children(null)
                        .build()
        );

        when(categoryProperties.getUrl()).thenReturn(apiUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(apiUrl)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Authorization", authorizationHeader)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(singleCategory));

        // When
        List<CategoryResponse> result = categoryGatewayDriver.getCategories(authorizationHeader);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("single1", result.get(0).getId());
        assertEquals("Single Category", result.get(0).getName());
        assertNull(result.get(0).getChildren());

        verify(categoryProperties).getUrl();
        verify(webClientBuilder).build();
    }

    @Test
    void getCategories_WhenDifferentAuthorizationHeaders_ShouldPassCorrectHeader() {
        // Given
        String authorizationHeader1 = "Bearer token1";
        String authorizationHeader2 = "Basic dXNlcjpwYXNz";
        String apiUrl = "https://api.example.com/categories";

        List<CategoryResponse> categories = Collections.emptyList();

        // Setup mocks for first call
        when(categoryProperties.getUrl()).thenReturn(apiUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(apiUrl)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(eq("Authorization"), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(categories));

        // When - First call
        categoryGatewayDriver.getCategories(authorizationHeader1);

        // Then - Verify first header
        verify(requestHeadersSpec).header("Authorization", authorizationHeader1);

        // When - Second call
        categoryGatewayDriver.getCategories(authorizationHeader2);

        // Then - Verify second header
        verify(requestHeadersSpec).header("Authorization", authorizationHeader2);
    }

    @Test
    void getCategories_WhenComplexCategoryStructure_ShouldReturnCorrectStructure() {
        // Given
        String authorizationHeader = "Bearer complexToken";
        String apiUrl = "https://api.example.com/categories";

        List<CategoryResponse> complexCategories = Arrays.asList(
                CategoryResponse.builder()
                        .id("parent1")
                        .name("Parent Category 1")
                        .children(Arrays.asList(
                                // Note: CategoryTree children would be here in real scenario
                        ))
                        .build(),
                CategoryResponse.builder()
                        .id("parent2")
                        .name("Parent Category 2")
                        .children(null)
                        .build()
        );

        when(categoryProperties.getUrl()).thenReturn(apiUrl);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(apiUrl)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Authorization", authorizationHeader)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(complexCategories));

        // When
        List<CategoryResponse> result = categoryGatewayDriver.getCategories(authorizationHeader);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        CategoryResponse parent1 = result.get(0);
        assertEquals("parent1", parent1.getId());
        assertEquals("Parent Category 1", parent1.getName());
        assertNotNull(parent1.getChildren());
        assertTrue(parent1.getChildren().isEmpty());

        CategoryResponse parent2 = result.get(1);
        assertEquals("parent2", parent2.getId());
        assertEquals("Parent Category 2", parent2.getName());
        assertNull(parent2.getChildren());

        verify(categoryProperties).getUrl();
        verify(webClientBuilder).build();
    }
}
