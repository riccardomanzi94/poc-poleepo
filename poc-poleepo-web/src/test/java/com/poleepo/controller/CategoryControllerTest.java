package com.poleepo.controller;

import com.poleepo.model.CategoryDto;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.retrievecategory.service.ICategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private ICategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void getCategoriesReturnsSuccessWithValidData() {
        // Given
        String store = "store123";
        String source = "web";
        String authorizationHeader = "Bearer token123";

        List<CategoryDto> mockCategories = Arrays.asList(
                CategoryDto.builder()
                        .name("Electronics")
                        .build(),
                CategoryDto.builder()
                        .name("Clothing")
                        .build()
        );

        when(categoryService.getCategory(store, source, authorizationHeader))
                .thenReturn(mockCategories);

        // When
        ResponseEntity<ResponseDto<List<CategoryDto>>> response =
                categoryController.getCategories(store, source, authorizationHeader);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseDto<List<CategoryDto>> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertNotNull(responseBody.getData());
        assertEquals(2, responseBody.getData().size());

        CategoryDto firstCategory = responseBody.getData().get(0);

        assertEquals("Electronics", firstCategory.getName());

        CategoryDto secondCategory = responseBody.getData().get(1);
        assertEquals("Clothing", secondCategory.getName());

        verify(categoryService).getCategory(store, source, authorizationHeader);
    }

    @Test
    void getCategoriesReturnsSuccessWithEmptyList() {
        // Given
        String store = "emptyStore";
        String source = "mobile";
        String authorizationHeader = "Bearer emptyToken";

        List<CategoryDto> emptyList = Collections.emptyList();

        when(categoryService.getCategory(store, source, authorizationHeader))
                .thenReturn(emptyList);

        // When
        ResponseEntity<ResponseDto<List<CategoryDto>>> response =
                categoryController.getCategories(store, source, authorizationHeader);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseDto<List<CategoryDto>> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertNotNull(responseBody.getData());
        assertTrue(responseBody.getData().isEmpty());

        verify(categoryService).getCategory(store, source, authorizationHeader);
    }

    @Test
    void getCategoriesWithDifferentStoreAndSource() {
        // Given
        String store = "anotherStore";
        String source = "api";
        String authorizationHeader = "Bearer anotherToken";

        List<CategoryDto> mockCategories = Arrays.asList(
                CategoryDto.builder()
                        .name("Books")
                        .build()
        );

        when(categoryService.getCategory(store, source, authorizationHeader))
                .thenReturn(mockCategories);

        // When
        ResponseEntity<ResponseDto<List<CategoryDto>>> response =
                categoryController.getCategories(store, source, authorizationHeader);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseDto<List<CategoryDto>> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals(1, responseBody.getData().size());
        assertEquals("Books", responseBody.getData().get(0).getName());

        verify(categoryService).getCategory(store, source, authorizationHeader);
    }

    @Test
    void getCategoriesVerifiesServiceCallWithCorrectParameters() {
        // Given
        String store = "testStore";
        String source = "testSource";
        String authorizationHeader = "Bearer testToken";

        List<CategoryDto> mockCategories = Collections.singletonList(
                CategoryDto.builder()
                        .name("Test Category")
                        .build()
        );

        when(categoryService.getCategory(store, source, authorizationHeader))
                .thenReturn(mockCategories);

        // When
        categoryController.getCategories(store, source, authorizationHeader);

        // Then
        verify(categoryService).getCategory(store, source, authorizationHeader);
    }
}
