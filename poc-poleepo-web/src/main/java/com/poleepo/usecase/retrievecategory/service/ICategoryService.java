package com.poleepo.usecase.retrievecategory.service;

import com.poleepo.usecase.retrievecategory.model.CategoryDto;
import lombok.NonNull;

import java.util.List;

public interface ICategoryService {

    List<CategoryDto> getCategory(@NonNull String storeId, @NonNull String source, String authorizationHeader);
}
