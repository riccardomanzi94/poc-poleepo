package com.poleepo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poleepo.model.CategoryDto;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.retrievecategory.service.ICategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICategoryService categoryService;

    private static final String BASE_URL = "/categories";
    private static final String X_STORE_HEADER = "X-STORE";
    private static final String X_SOURCE_HEADER = "X-SOURCE";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Test
    void getCategories_WhenValidRequest_ShouldReturnSuccessResponse() throws Exception {
        // Given
        String store = "store123";
        String source = "web";
        String authorization = "Bearer token123";

        List<CategoryDto> mockCategories = Arrays.asList(
                CategoryDto.builder()
                        .id("cat1")
                        .name("Electronics")
                        .build(),
                CategoryDto.builder()
                        .id("cat2")
                        .name("Clothing")
                        .build(),
                CategoryDto.builder()
                        .id("cat3")
                        .name("Books")
                        .build()
        );

        when(categoryService.getCategory(eq(store), eq(source), eq(authorization)))
                .thenReturn(mockCategories);

        // When & Then
        MvcResult result = mockMvc.perform(get(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .header(AUTHORIZATION_HEADER, authorization))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].id").value("cat1"))
                .andExpect(jsonPath("$.data[0].name").value("Electronics"))
                .andExpect(jsonPath("$.data[1].id").value("cat2"))
                .andExpect(jsonPath("$.data[1].name").value("Clothing"))
                .andExpect(jsonPath("$.data[2].id").value("cat3"))
                .andExpect(jsonPath("$.data[2].name").value("Books"))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.message").isEmpty())
                .andReturn();

        // Verify service interaction
        verify(categoryService).getCategory(store, source, authorization);

        // Additional response validation
        String responseContent = result.getResponse().getContentAsString();
        ResponseDto<?> responseDto = objectMapper.readValue(responseContent, ResponseDto.class);
        assertTrue(responseDto.isSuccess());
        assertNotNull(responseDto.getData());
    }

    @Test
    void getCategories_WhenEmptyList_ShouldReturnSuccessWithEmptyArray() throws Exception {
        // Given
        String store = "emptyStore";
        String source = "mobile";
        String authorization = "Bearer emptyToken";

        List<CategoryDto> emptyCategories = Collections.emptyList();

        when(categoryService.getCategory(eq(store), eq(source), eq(authorization)))
                .thenReturn(emptyCategories);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .header(AUTHORIZATION_HEADER, authorization))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.error").value(0));

        verify(categoryService).getCategory(store, source, authorization);
    }

    @Test
    void getCategories_WhenMissingStoreHeader_ShouldReturnBadRequest() throws Exception {
        // Given
        String source = "web";
        String authorization = "Bearer token123";

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .header(X_SOURCE_HEADER, source)
                        .header(AUTHORIZATION_HEADER, authorization))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategories_WhenMissingSourceHeader_ShouldReturnBadRequest() throws Exception {
        // Given
        String store = "store123";
        String authorization = "Bearer token123";

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(AUTHORIZATION_HEADER, authorization))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategories_WhenEmptyHeaders_ShouldCallServiceWithEmptyValues() throws Exception {
        // Given
        List<CategoryDto> emptyCategories = Collections.emptyList();

        when(categoryService.getCategory(eq(""), eq(""), eq("")))
                .thenReturn(emptyCategories);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .header(X_STORE_HEADER, "")
                        .header(X_SOURCE_HEADER, "")
                        .header(AUTHORIZATION_HEADER, ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.error").value(0));

        verify(categoryService).getCategory("", "", "");
    }

    @Test
    void getCategories_WhenSingleCategory_ShouldReturnCorrectResponse() throws Exception {
        // Given
        String store = "singleStore";
        String source = "api";
        String authorization = "Bearer singleToken";

        List<CategoryDto> singleCategory = Collections.singletonList(
                CategoryDto.builder()
                        .id("single1")
                        .name("Single Category")
                        .build()
        );

        when(categoryService.getCategory(eq(store), eq(source), eq(authorization)))
                .thenReturn(singleCategory);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .header(AUTHORIZATION_HEADER, authorization))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value("single1"))
                .andExpect(jsonPath("$.data[0].name").value("Single Category"));

        verify(categoryService).getCategory(store, source, authorization);
    }

    @Test
    void getCategories_WhenDifferentStoreValues_ShouldCallServiceWithCorrectParameters() throws Exception {
        // Given
        String store1 = "testStore1";
        String store2 = "testStore2";
        String source = "web";
        String authorization = "Bearer testToken";

        List<CategoryDto> categories = Collections.singletonList(
                CategoryDto.builder()
                        .id("test1")
                        .name("Test Category")
                        .build()
        );

        when(categoryService.getCategory(eq(store1), eq(source), eq(authorization)))
                .thenReturn(categories);
        when(categoryService.getCategory(eq(store2), eq(source), eq(authorization)))
                .thenReturn(categories);

        // When & Then - First call
        mockMvc.perform(get(BASE_URL)
                        .header(X_STORE_HEADER, store1)
                        .header(X_SOURCE_HEADER, source)
                        .header(AUTHORIZATION_HEADER, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // When & Then - Second call
        mockMvc.perform(get(BASE_URL)
                        .header(X_STORE_HEADER, store2)
                        .header(X_SOURCE_HEADER, source)
                        .header(AUTHORIZATION_HEADER, authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify both calls
        verify(categoryService).getCategory(store1, source, authorization);
        verify(categoryService).getCategory(store2, source, authorization);
    }

    @Test
    void getCategories_WhenCategoryWithSpecialCharacters_ShouldReturnCorrectResponse() throws Exception {
        // Given
        String store = "specialStore";
        String source = "web";
        String authorization = "Bearer specialToken";

        List<CategoryDto> specialCategories = Arrays.asList(
                CategoryDto.builder()
                        .id("cat-with-dash")
                        .name("Category with Special Ch@rs & Symbols!")
                        .build(),
                CategoryDto.builder()
                        .id("cat_with_underscore")
                        .name("Catégorie avec accents éèà")
                        .build()
        );

        when(categoryService.getCategory(eq(store), eq(source), eq(authorization)))
                .thenReturn(specialCategories);

        // When & Then
        mockMvc.perform(get(BASE_URL)
                        .header(X_STORE_HEADER, store)
                        .header(X_SOURCE_HEADER, source)
                        .header(AUTHORIZATION_HEADER, authorization))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("cat-with-dash"))
                .andExpect(jsonPath("$.data[0].name").value("Category with Special Ch@rs & Symbols!"))
                .andExpect(jsonPath("$.data[1].id").value("cat_with_underscore"))
                .andExpect(jsonPath("$.data[1].name").value("Catégorie avec accents éèà"));

        verify(categoryService).getCategory(store, source, authorization);
    }
}
