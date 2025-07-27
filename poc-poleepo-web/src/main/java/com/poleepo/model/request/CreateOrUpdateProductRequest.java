package com.poleepo.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateProductRequest {
    private Information information;
    private Offer offer;
    private Integer shopId;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Information {
        private String title;
        private String category;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Offer {
        private Double price;
        private Double vat;
    }
}