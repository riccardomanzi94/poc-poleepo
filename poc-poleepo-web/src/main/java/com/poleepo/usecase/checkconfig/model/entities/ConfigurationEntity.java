package com.poleepo.usecase.checkconfig.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuration")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "source", nullable = false)
    private Long source;

    @Column(name = "api_token", nullable = false, length = 50)
    private String apiToken;

    @Column(name = "shop_id", nullable = false, length = 10)
    private String shopId;
}
