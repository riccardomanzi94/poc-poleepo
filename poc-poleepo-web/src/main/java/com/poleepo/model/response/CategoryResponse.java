package com.poleepo.model.response;

import com.poleepo.model.CategoryDto;
import com.poleepo.model.CategoryTree;
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
