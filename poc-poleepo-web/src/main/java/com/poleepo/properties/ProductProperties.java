package com.poleepo.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated
@ConfigurationProperties(prefix = "product.config")
public class ProductProperties {

    private String urlCreate;
    private String urlUpdate;
    private String urlUpdateQuantity;
    private String defaultToken;
    private String availableToken;
}
