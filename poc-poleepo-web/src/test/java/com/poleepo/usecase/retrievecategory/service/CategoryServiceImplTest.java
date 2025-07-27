package com.poleepo.usecase.retrievecategory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poleepo.exception.GenericException;
import com.poleepo.usecase.retrievecategory.model.CategoryDto;
import com.poleepo.properties.CategoryProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CategoryServiceImplTest {

    @Mock
    private ICategoryGatewayDriver categoryGatewayDriver;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private CategoryProperties categoryProperties;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCategory_tokenValido() {
        String storeId = "1";
        String source = "test";
        String token = "token1";
        String availableTokens = "token1,token2";
        when(categoryProperties.getAvailableToken()).thenReturn(availableTokens);
        when(categoryProperties.getDefaultToken()).thenReturn("token1");
        when(categoryGatewayDriver.getCategories(token)).thenReturn(Collections.emptyList());

        // Correzione: passare solo il token senza "Bearer "
        List<CategoryDto> result = categoryService.getCategory(storeId, source, "token1");
        assertNotNull(result);
        verify(categoryGatewayDriver).getCategories(token);
    }

    @Test
    void getCategory_tokenNonValido() {
        String storeId = "1";
        String source = "test";
        String token = "token3";
        String availableTokens = "token1,token2";
        when(categoryProperties.getAvailableToken()).thenReturn(availableTokens);
        when(categoryProperties.getDefaultToken()).thenReturn("token1");

        assertThrows(GenericException.class, () ->
                categoryService.getCategory(storeId, source, "Bearer token3")
        );
    }

    @Test
    void getCategory_tokenNull() {
        String storeId = "1";
        String source = "test";
        String defaultToken = "token1";
        when(categoryProperties.getDefaultToken()).thenReturn(defaultToken);
        when(categoryProperties.getAvailableToken()).thenReturn(defaultToken);
        when(categoryGatewayDriver.getCategories(defaultToken)).thenReturn(Collections.emptyList());

        List<CategoryDto> result = categoryService.getCategory(storeId, source, null);
        assertNotNull(result);
        verify(categoryGatewayDriver).getCategories(defaultToken);
    }
}
