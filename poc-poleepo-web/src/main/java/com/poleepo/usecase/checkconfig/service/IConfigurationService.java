package com.poleepo.usecase.checkconfig.service;

import com.poleepo.model.request.ConfigurationRequest;
import lombok.NonNull;

public interface IConfigurationService {

    boolean createOrUpdateConfiguration(@NonNull String store, @NonNull String source, @NonNull ConfigurationRequest configurationRequest);
}
