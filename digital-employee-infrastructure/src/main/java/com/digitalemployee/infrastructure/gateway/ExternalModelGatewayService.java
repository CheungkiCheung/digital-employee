package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;

public class ExternalModelGatewayService {

    public ExternalModelGatewayResponseDTO complete(String provider, String baseUrl, ExternalModelGatewayRequestDTO request) {
        requireText(provider, "provider");
        requireText(baseUrl, "baseUrl");
        if (request == null) {
            throw new IllegalArgumentException("external model gateway request is required");
        }
        requireText(request.getModel(), "model");
        requireText(request.getInput(), "input");

        return ExternalModelGatewayResponseDTO.builder()
                .answer("external model gateway is configured but network execution is disabled: "
                        + provider + "/" + request.getModel() + " @ " + baseUrl)
                .build();
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("external model gateway " + fieldName + " is required");
        }
    }

}
