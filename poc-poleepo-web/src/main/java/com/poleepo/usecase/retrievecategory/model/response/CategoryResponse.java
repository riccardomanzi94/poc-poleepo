package com.poleepo.usecase.retrievecategory.model.response;

import com.poleepo.usecase.retrievecategory.model.CategoryTree;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private String id;
    private String name;
    private List<CategoryTree> children;

}
