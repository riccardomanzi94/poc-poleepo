package com.poleepo.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated
@ConfigurationProperties(prefix = "category.config")
public class CategoryProperties {

    private String url;
    private String token;
}
