package com.poleepo.usecase.retrievecategory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTree {

    private String id;
    private String name;
    private List<CategoryDto> children;

}
