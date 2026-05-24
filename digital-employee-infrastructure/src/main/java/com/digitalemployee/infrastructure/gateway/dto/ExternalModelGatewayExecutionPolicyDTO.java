package com.digitalemployee.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ExternalModelGatewayExecutionPolicyDTO implements Serializable {

    private static final long serialVersionUID = 3920299144716541304L;

    private int timeoutMs;
    private int retryAttempts;

}
