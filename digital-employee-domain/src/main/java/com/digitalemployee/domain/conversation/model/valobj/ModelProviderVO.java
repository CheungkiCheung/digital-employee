package com.digitalemployee.domain.conversation.model.valobj;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModelProviderVO {

    private final String provider;
    private final String model;
    private final boolean external;
    private final String apiKeyEnvName;

}
