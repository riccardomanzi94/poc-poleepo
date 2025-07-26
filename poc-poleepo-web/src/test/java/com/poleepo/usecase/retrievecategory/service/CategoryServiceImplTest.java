package com.poleepo.usecase.retrievecategory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poleepo.exception.GenericException;
import com.poleepo.model.CategoryDto;
import com.poleepo.model.CategoryTree;
import com.poleepo.model.response.CategoryResponse;
import com.poleepo.properties.CategoryProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private ICategoryGatewayDriver categoryGatewayDriver;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private CategoryProperties categoryProperties;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getCategory_WhenValidTokenWithBearer_ShouldReturnCategories() {
        // Given
        String storeId = "store123";
        String source = "web";
        String authorizationHeader = "Bearer validToken";
        String validTokens = "validToken,anotherToken";

        // Mock CategoryDto objects that will be children
        CategoryDto childCategory1 = CategoryDto.builder()
                .id("child1")
                .name("Child Category 1")
                .build();

        CategoryDto childCategory2 = CategoryDto.builder()
                .id("child2")
                .name("Child Category 2")
                .build();

        // Mock CategoryDto that will be converted from CategoryTree without children
        CategoryDto convertedCategory = CategoryDto.builder()
                .id("converted1")
                .name("Converted Category")
                .build();

        // CategoryTree without children (will be converted)
        CategoryTree treeWithoutChildren = CategoryTree.builder()
                .id("tree1")
                .name("Tree Without Children")
                .children(null)
                .build();

        // CategoryTree with children (children will be added directly)
        CategoryTree treeWithChildren = CategoryTree.builder()
                .id("tree2")
                .name("Tree With Children")
                .children(Arrays.asList(childCategory1, childCategory2))
                .build();

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id("root1")
                .name("Root Category")
                .children(Arrays.asList(treeWithoutChildren, treeWithChildren))
                .build();

        List<CategoryResponse> gatewayResponse = Collections.singletonList(categoryResponse);

        // Setup mocks
        when(categoryProperties.getDefaultToken()).thenReturn(validTokens);
        when(categoryGatewayDriver.getCategories(authorizationHeader)).thenReturn(gatewayResponse);
        when(mapper.convertValue(eq(treeWithoutChildren), eq(CategoryDto.class))).thenReturn(convertedCategory);

        // When
        List<CategoryDto> result = categoryService.getCategory(storeId, source, authorizationHeader);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size()); // 2 children + 1 converted
        assertTrue(result.contains(childCategory1));
        assertTrue(result.contains(childCategory2));
        assertTrue(result.contains(convertedCategory));

        verify(categoryProperties,times(2)).getDefaultToken();
        verify(categoryGatewayDriver,times(1)).getCategories(authorizationHeader);
        verify(mapper).convertValue(treeWithoutChildren, CategoryDto.class);
    }

    @Test
    void getCategory_WhenValidTokenWithoutBearer_ShouldReturnCategories() {
        // Given
        String storeId = "store456";
        String source = "mobile";
        String authorizationHeader = "directToken";
        String validTokens = "directToken,otherToken";

        CategoryDto convertedCategory = CategoryDto.builder()
                .id("converted1")
                .name("Converted Category")
                .build();

        CategoryTree treeWithoutChildren = CategoryTree.builder()
                .id("tree1")
                .name("Simple Tree")
                .children(null)
                .build();

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id("root1")
                .name("Root Category")
                .children(Collections.singletonList(treeWithoutChildren))
                .build();

        // Setup mocks
        when(categoryProperties.getDefaultToken()).thenReturn(validTokens);
        when(categoryGatewayDriver.getCategories(authorizationHeader)).thenReturn(Collections.singletonList(categoryResponse));
        when(mapper.convertValue(eq(treeWithoutChildren), eq(CategoryDto.class))).thenReturn(convertedCategory);

        // When
        List<CategoryDto> result = categoryService.getCategory(storeId, source, authorizationHeader);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(convertedCategory, result.get(0));

        verify(categoryProperties,times(2)).getDefaultToken();
        verify(categoryGatewayDriver,times(1)).getCategories(authorizationHeader);
    }

    @Test
    void getCategory_WhenInvalidToken_ShouldThrowGenericException() {
        // Given
        String storeId = "store789";
        String source = "api";
        String authorizationHeader = "Bearer invalidToken";
        String validTokens = "validToken1,validToken2";

        when(categoryProperties.getDefaultToken()).thenReturn(validTokens);

        // When & Then
        GenericException exception = assertThrows(GenericException.class,
            () -> categoryService.getCategory(storeId, source, authorizationHeader));

        assertEquals("Token non valido", exception.getMessage());
        verify(categoryProperties,times(2)).getDefaultToken();
    }

    @Test
    void getCategory_WhenNullTokenProperty_ShouldThrowGenericException() {
        // Given
        String storeId = "store999";
        String source = "web";
        String authorizationHeader = "Bearer anyToken";

        when(categoryProperties.getDefaultToken()).thenReturn(null);

        // When & Then
        GenericException exception = assertThrows(GenericException.class,
            () -> categoryService.getCategory(storeId, source, authorizationHeader));

        assertEquals("Token non valido", exception.getMessage());
        verify(categoryProperties).getDefaultToken();
    }

    @Test
    void getCategory_WhenEmptyGatewayResponse_ShouldReturnEmptyList() {
        // Given
        String storeId = "emptyStore";
        String source = "web";
        String authorizationHeader = "Bearer validToken";
        String validTokens = "validToken";

        when(categoryProperties.getDefaultToken()).thenReturn(validTokens);
        when(categoryGatewayDriver.getCategories(authorizationHeader)).thenReturn(Collections.emptyList());

        // When
        List<CategoryDto> result = categoryService.getCategory(storeId, source, authorizationHeader);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryProperties,times(2)).getDefaultToken();
        verify(categoryGatewayDriver,times(1)).getCategories(authorizationHeader);
    }

    @Test
    void getCategory_WhenCategoriesWithoutChildren_ShouldReturnEmptyList() {
        // Given
        String storeId = "noChildrenStore";
        String source = "api";
        String authorizationHeader = "Bearer validToken";
        String validTokens = "validToken";

        CategoryResponse categoryWithoutChildren = CategoryResponse.builder()
                .id("root1")
                .name("Root Without Children")
                .children(null)
                .build();

        when(categoryProperties.getDefaultToken()).thenReturn(validTokens);
        when(categoryGatewayDriver.getCategories(authorizationHeader))
                .thenReturn(Arrays.asList(categoryWithoutChildren));

        // When
        List<CategoryDto> result = categoryService.getCategory(storeId, source, authorizationHeader);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryProperties,times(2)).getDefaultToken();
        verify(categoryGatewayDriver,times(1)).getCategories(authorizationHeader);
    }

    @Test
    void getCategory_WhenComplexHierarchy_ShouldProcessCorrectly() {
        // Given
        String storeId = "complexStore";
        String source = "web";
        String authorizationHeader = "Bearer validToken";
        String validTokens = "validToken";

        // Create CategoryDto objects that will be direct children
        CategoryDto directChild1 = CategoryDto.builder()
                .id("direct1")
                .name("Direct Child 1")
                .build();

        CategoryDto directChild2 = CategoryDto.builder()
                .id("direct2")
                .name("Direct Child 2")
                .build();

        // Create CategoryDto that will be converted from CategoryTree
        CategoryDto convertedCategory = CategoryDto.builder()
                .id("converted1")
                .name("Converted Category")
                .build();

        // Create tree structure
        CategoryTree leafTree = CategoryTree.builder()
                .id("leafTree")
                .name("Leaf Tree")
                .children(null)
                .build();

        CategoryTree treeWithChildren = CategoryTree.builder()
                .id("treeWithChildren")
                .name("Tree With Children")
                .children(Arrays.asList(directChild1, directChild2))
                .build();

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id("root1")
                .name("Root 1")
                .children(Arrays.asList(leafTree, treeWithChildren))
                .build();

        List<CategoryResponse> gatewayResponse = Collections.singletonList(categoryResponse);

        // Setup mocks
        when(categoryProperties.getDefaultToken()).thenReturn(validTokens);
        when(categoryGatewayDriver.getCategories(authorizationHeader)).thenReturn(gatewayResponse);
        when(mapper.convertValue(eq(leafTree), eq(CategoryDto.class))).thenReturn(convertedCategory);

        // When
        List<CategoryDto> result = categoryService.getCategory(storeId, source, authorizationHeader);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size()); // 2 direct children + 1 converted

        // Verify all expected categories are present
        assertTrue(result.contains(directChild1));
        assertTrue(result.contains(directChild2));
        assertTrue(result.contains(convertedCategory));

        verify(categoryProperties,times(2)).getDefaultToken();
        verify(categoryGatewayDriver).getCategories(authorizationHeader);
        verify(mapper).convertValue(leafTree, CategoryDto.class);
    }

    @Test
    void getCategory_WhenTokenInMiddleOfCommaList_ShouldBeValid() {
        // Given
        String storeId = "middleStore";
        String source = "web";
        String authorizationHeader = "Bearer middleToken";
        String validTokens = "firstToken,middleToken,lastToken";

        CategoryDto convertedCategory = CategoryDto.builder()
                .id("converted1")
                .name("Converted Category")
                .build();

        CategoryTree tree = CategoryTree.builder()
                .id("tree1")
                .name("Tree 1")
                .children(null)
                .build();

        CategoryResponse response = CategoryResponse.builder()
                .id("root1")
                .name("Root")
                .children(Collections.singletonList(tree))
                .build();

        when(categoryProperties.getDefaultToken()).thenReturn(validTokens);
        when(categoryGatewayDriver.getCategories(authorizationHeader)).thenReturn(Collections.singletonList(response));
        when(mapper.convertValue(eq(tree), eq(CategoryDto.class))).thenReturn(convertedCategory);

        // When
        List<CategoryDto> result = categoryService.getCategory(storeId, source, authorizationHeader);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(convertedCategory, result.get(0));

        verify(categoryProperties,times(2)).getDefaultToken();
        verify(categoryGatewayDriver).getCategories(authorizationHeader);
    }

    @Test
    void getCategory_WhenMapperConvertsCorrectly_ShouldReturnMappedValues() {
        // Given
        String storeId = "mapperStore";
        String source = "web";
        String authorizationHeader = "Bearer validToken";
        String validTokens = "validToken";

        CategoryDto mappedCategory1 = CategoryDto.builder()
                .id("mapped1")
                .name("Mapped Category 1")
                .build();

        CategoryDto mappedCategory2 = CategoryDto.builder()
                .id("mapped2")
                .name("Mapped Category 2")
                .build();

        CategoryTree tree1 = CategoryTree.builder()
                .id("original1")
                .name("Original 1")
                .children(null)
                .build();

        CategoryTree tree2 = CategoryTree.builder()
                .id("original2")
                .name("Original 2")
                .children(null)
                .build();

        CategoryResponse response = CategoryResponse.builder()
                .id("root")
                .name("Root")
                .children(Arrays.asList(tree1, tree2))
                .build();

        when(categoryProperties.getDefaultToken()).thenReturn(validTokens);
        when(categoryGatewayDriver.getCategories(authorizationHeader)).thenReturn(Collections.singletonList(response));
        when(mapper.convertValue(eq(tree1), eq(CategoryDto.class))).thenReturn(mappedCategory1);
        when(mapper.convertValue(eq(tree2), eq(CategoryDto.class))).thenReturn(mappedCategory2);

        // When
        List<CategoryDto> result = categoryService.getCategory(storeId, source, authorizationHeader);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mappedCategory1));
        assertTrue(result.contains(mappedCategory2));

        verify(mapper).convertValue(tree1, CategoryDto.class);
        verify(mapper).convertValue(tree2, CategoryDto.class);
    }
}
