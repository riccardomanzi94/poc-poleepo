package com.poleepo.usecase.retrievecategory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poleepo.exception.GenericException;
import com.poleepo.model.CategoryDto;
import com.poleepo.model.CategoryTree;
import com.poleepo.model.response.CategoryResponse;
import com.poleepo.properties.CategoryProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService{

    private final ICategoryGatewayDriver categoryGatewayDriver;
    private final ObjectMapper mapper;
    private final CategoryProperties categoryProperties;


    @Override
    public List<CategoryDto> getCategory(@NonNull String storeId, @NonNull String source, String authorizationHeader) {
        log.info("Inizio getCategory per storeId: {}, source: {}", storeId, source);

        if(authorizationHeader != null){
            String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
            if (categoryProperties.getDefaultToken() == null || !List.of(categoryProperties.getAvailableToken().split(",")).contains(token)) {
                throw new GenericException("Token non valido");
            }
        }else{
            authorizationHeader = categoryProperties.getDefaultToken();
        }
        List<CategoryResponse> categories = categoryGatewayDriver.getCategories(authorizationHeader);
        List<CategoryDto> leafCategories = new ArrayList<>();
        AtomicInteger sourceIdCounter = new AtomicInteger(1);

        for (CategoryResponse rootCategory : categories) {
            if (rootCategory.getChildren() != null) {
                for (CategoryTree rootTree : rootCategory.getChildren()) {
                    buildLeafDtos(rootTree, "root/" + rootCategory.getName(), leafCategories, sourceIdCounter);
                }
            }else {
                CategoryDto dto = new CategoryDto();
                dto.setName(rootCategory.getName());
                dto.setPath("root/" + rootCategory.getName());
                dto.setSourceId(rootCategory.getId());
                leafCategories.add(dto);
            }
        }

        List<CategoryDto> response = new ArrayList<>(leafCategories);

        log.info("Fine getCategory, categorie restituite: {}", response.size());
        return response;
    }

    private void buildLeafDtos(CategoryTree node, String path, List<CategoryDto> result, AtomicInteger sourceIdCounter) {
        String currentPath = path + "/" + node.getName();
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            CategoryDto dto = new CategoryDto();
            dto.setName(node.getName());
            dto.setPath(currentPath);
            dto.setSourceId(node.getId());
            result.add(dto);
        } else {
            for (CategoryDto child : node.getChildren()) {
                CategoryTree childTree = mapper.convertValue(child, CategoryTree.class);
                buildLeafDtos(childTree, currentPath, result, sourceIdCounter);
            }
        }
    }
}
