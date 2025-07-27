package com.poleepo.controller;

import com.poleepo.usecase.checkconfig.model.request.ConfigurationRequest;
import com.poleepo.model.response.ResponseDto;
import com.poleepo.usecase.checkconfig.service.IConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.poleepo.config.CostantConfig.*;

@RestController
@RequestMapping(BASE_URI + "/configurations")
@Tag(name = "Configurazioni", description = "Operazioni sulle configurazioni")
@RequiredArgsConstructor
public class ConfigurationController {

    private final IConfigurationService configurationService;

    @Operation(summary = "Crea o aggiorna una configurazione", description = "Crea o aggiorna la configurazione per uno store e una source specifici.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configurazione creata/aggiornata con successo"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida")
    })
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
