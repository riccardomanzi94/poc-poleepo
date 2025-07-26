package com.poleepo.controller;

import com.poleepo.model.CategoryDto;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.retrievecategory.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.poleepo.config.CostantConfig.*;

@RestController
@RequestMapping(BASE_URI)
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<ResponseDto<List<CategoryDto>>> getCategories(
            @RequestHeader(X_STORE) String store,
            @RequestHeader(X_SOURCE) String source,
            @RequestHeader("Authorization") String authorizationHeader) {

        List<CategoryDto> category = categoryService.getCategory(store, source,authorizationHeader);


        return ResponseEntity.ok(ResponseDto.<List<CategoryDto>>builder()
                .success(true)
                .data(category)
                .build());
    }
}
