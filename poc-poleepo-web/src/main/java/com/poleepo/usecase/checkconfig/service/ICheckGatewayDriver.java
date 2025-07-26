package com.poleepo.usecase.checkconfig.service;

import com.poleepo.model.CheckConfigResponseDto;
import com.poleepo.model.request.ConfigurationRequest;
import lombok.NonNull;

public interface ICheckGatewayDriver {

    CheckConfigResponseDto callCheckConfig(@NonNull String store, @NonNull String source, @NonNull ConfigurationRequest configurationRequest);


}
