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
@ConfigurationProperties(prefix = "check.config")
public class CheckConfigProperties {

    private String url;
    private String availableToken;
}
