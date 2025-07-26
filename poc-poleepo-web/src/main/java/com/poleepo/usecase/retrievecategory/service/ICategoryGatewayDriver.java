package com.poleepo.usecase.retrievecategory.service;

import com.poleepo.model.response.CategoryResponse;

import java.util.List;

public interface ICategoryGatewayDriver {

    List<CategoryResponse> getCategories();
}
