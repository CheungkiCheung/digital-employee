package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayExecutionPolicyDTO;

public class ExternalModelGatewayService {

    public ExternalModelGatewayResponseDTO complete(String provider,
                                                    String baseUrl,
                                                    ExternalModelGatewayExecutionPolicyDTO executionPolicy,
                                                    ExternalModelGatewayRequestDTO request) {
        requireText(provider, "provider");
        requireText(baseUrl, "baseUrl");
        requireExecutionPolicy(executionPolicy);
        if (request == null) {
            throw new IllegalArgumentException("external model gateway request is required");
        }
        requireText(request.getModel(), "model");
        requireText(request.getInput(), "input");

        return ExternalModelGatewayResponseDTO.builder()
                .answer("external model gateway is configured but network execution is disabled: "
                        + provider + "/" + request.getModel() + " @ " + baseUrl
                        + " timeoutMs=" + executionPolicy.getTimeoutMs()
                        + " retryAttempts=" + executionPolicy.getRetryAttempts())
                .build();
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("external model gateway " + fieldName + " is required");
        }
    }

    private void requireExecutionPolicy(ExternalModelGatewayExecutionPolicyDTO executionPolicy) {
        if (executionPolicy == null
                || executionPolicy.getTimeoutMs() <= 0
                || executionPolicy.getRetryAttempts() < 0) {
            throw new IllegalArgumentException("external model gateway execution policy is invalid");
        }
    }

}
