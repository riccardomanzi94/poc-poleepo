package com.poleepo.usecase.checkconfig.model.response;

import com.poleepo.usecase.retrievecategory.model.ShopDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckConfigResponseDto {

    private boolean active;
    private List<ShopDto> shops;
}
