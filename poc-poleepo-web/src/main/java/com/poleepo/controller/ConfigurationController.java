package com.poleepo.controller;

import com.poleepo.model.request.ConfigurationRequest;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.checkconfig.service.IConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.poleepo.config.CostantConfig.*;

@RestController
@RequestMapping(BASE_URI + "/configurations")
@RequiredArgsConstructor
public class ConfigurationController {

    private final IConfigurationService configurationService;

    @PostMapping
    public ResponseEntity<ResponseDto<String>> createConfiguration(
            @RequestHeader(X_STORE) String store,
            @RequestHeader(X_SOURCE) String source,
            @Valid @RequestBody ConfigurationRequest request) {

        boolean response = configurationService.createOrUpdateConfiguration(store, source, request);

        return ResponseEntity.ok(ResponseDto.<String>builder()
                .success(true)
                .data(request.getShopId())
                .build());
    }
}
