package com.poleepo.usecase.updateproduct.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuantityRequest {

    private Integer quantity;
    private Integer shopId;
}
