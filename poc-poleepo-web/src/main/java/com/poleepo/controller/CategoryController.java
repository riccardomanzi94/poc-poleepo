package com.poleepo.controller;

import com.poleepo.model.CategoryDto;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.retrievecategory.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categorie", description = "Operazioni sulle categorie")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(
            summary = "Recupera le categorie",
            description = "Restituisce la lista delle categorie disponibili per uno store e una source specifici.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successo"),
                    @ApiResponse(responseCode = "401", description = "Non autorizzato"),
                    @ApiResponse(responseCode = "500", description = "Errore interno")
            }
    )
    @GetMapping("/categories")
    public ResponseEntity<ResponseDto<List<CategoryDto>>> getCategories(
            @RequestHeader(X_STORE) String store,
            @RequestHeader(X_SOURCE) String source,
            @RequestHeader(value = "Authorization",required = false) String authorizationHeader) {

        List<CategoryDto> category = categoryService.getCategory(store, source,authorizationHeader);


        return ResponseEntity.ok(ResponseDto.<List<CategoryDto>>builder()
                .success(true)
                .data(category)
                .build());
    }
}
