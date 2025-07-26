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
@ConfigurationProperties(prefix = "category.config")
public class CategoryProperties {

    private String url;
    private String defaultToken;
    private String availableToken;
}
