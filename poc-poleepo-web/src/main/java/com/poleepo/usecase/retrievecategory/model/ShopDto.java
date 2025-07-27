package com.poleepo.usecase.retrievecategory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopDto {

    private String id;
    private String name;
    private String status;
    private String vatNumber;
}
