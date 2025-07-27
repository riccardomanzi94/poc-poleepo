package com.poleepo.controller;

import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.updateproduct.model.request.ProductRequest;
import com.poleepo.usecase.updateproduct.model.response.ProductResponse;
import com.poleepo.usecase.updateproduct.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.poleepo.config.CostantConfig.*;

@RestController
@RequestMapping(BASE_URI + "/products")
@Tag(name = "Prodotto", description = "Operazioni sul prodotto")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @Operation(summary = "Crea o aggiorna un prodotto", description = "Crea o aggiorna un prodotto per uno store e una source specifici.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prodotto creato/aggiornato con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida")
    })
    @PutMapping
    public ResponseEntity<ResponseDto<ProductResponse>> createOrUpdateProduct(
            @RequestHeader(X_STORE) String store,
            @RequestHeader(X_SOURCE) String source,
            @Valid @RequestBody ProductRequest request,
            @RequestHeader(value = "Authorization",required = false) String authorizationHeader){

        ProductResponse response = productService.createOrUpdateProduct(request, source, store, authorizationHeader);

        return ResponseEntity.ok(ResponseDto.<ProductResponse>builder()
                .success(true)
                .data(response)
                .build());

    }
}
