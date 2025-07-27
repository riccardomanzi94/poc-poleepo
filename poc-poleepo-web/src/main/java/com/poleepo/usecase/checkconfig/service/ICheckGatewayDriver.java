package com.poleepo.usecase.checkconfig.service;

import com.poleepo.usecase.checkconfig.model.response.CheckConfigResponseDto;
import com.poleepo.usecase.checkconfig.model.request.ConfigurationRequest;
import lombok.NonNull;

public interface ICheckGatewayDriver {

    CheckConfigResponseDto callCheckConfig(@NonNull String store, @NonNull String source, @NonNull ConfigurationRequest configurationRequest);


}
