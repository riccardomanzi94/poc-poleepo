package com.poleepo.usecase.retrievecategory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private String name;
    private String path;
    private String sourceId;
    private String id;
}
