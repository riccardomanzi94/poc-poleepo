package com.poleepo.usecase.updateproduct.model.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

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
    private Integer sourceId;
}
