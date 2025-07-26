package com.poleepo.usecase.retrievecategory.service;

import com.poleepo.model.CategoryDto;
import com.poleepo.model.response.CategoryResponse;
import lombok.NonNull;

import java.util.List;

public interface ICategoryService {

    List<CategoryDto> getCategory(@NonNull String storeId, @NonNull String source, String authorizationHeader);
}
