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

        List<CategoryDto> response = new ArrayList<>();

        if(authorizationHeader != null){
            String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7) : authorizationHeader;
            if (categoryProperties.getDefaultToken() == null || !List.of(categoryProperties.getAvailableToken().split(",")).contains(token)) {
                throw new GenericException("Token non valido");
            }
        }else{
            authorizationHeader = categoryProperties.getDefaultToken();
        }


        List<CategoryResponse> categories = categoryGatewayDriver.getCategories(authorizationHeader);
        log.debug("Categorie recuperate: {}", categories.size());

        List<CategoryTree> allChildren = categories.stream()
                .filter(c -> c.getChildren() != null)
                .flatMap(c -> c.getChildren().stream())
                .toList();
        log.debug("Numero totale di children trovati: {}", allChildren.size());

        List<CategoryDto> categoryWithOutChildren = allChildren.stream()
                .filter(c -> c.getChildren() == null)
                .map(c -> mapper.convertValue(c, CategoryDto.class))
                .toList();
        log.debug("Categorie senza children: {}", categoryWithOutChildren.size());

        allChildren.stream()
                .filter(c -> c.getChildren() != null)
                .forEach(c -> response.addAll(c.getChildren()));

        response.addAll(categoryWithOutChildren);

        log.info("Fine getCategory, categorie restituite: {}", response.size());
        return response;
    }
}
