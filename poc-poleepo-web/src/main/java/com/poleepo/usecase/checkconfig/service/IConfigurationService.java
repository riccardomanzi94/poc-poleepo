package com.poleepo.usecase.checkconfig.service;

import com.poleepo.usecase.checkconfig.model.request.ConfigurationRequest;
import lombok.NonNull;

public interface IConfigurationService {

    boolean createOrUpdateConfiguration(@NonNull String store, @NonNull String source, @NonNull ConfigurationRequest configurationRequest);
}
