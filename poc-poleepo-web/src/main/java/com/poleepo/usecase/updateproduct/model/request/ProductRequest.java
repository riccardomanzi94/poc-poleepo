package com.poleepo.usecase.updateproduct.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotNull
    private String title;
    @NotNull
    private String categorySourceId;
    @NotNull
    private Double price;
    @NotNull
    private Double vatRate;
    @NotNull
    private Integer quantity;
    private String sourceId;
}
