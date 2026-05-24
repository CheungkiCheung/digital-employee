package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayExecutionPolicyDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayToolDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionMessageDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionToolDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionToolFunctionDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ExternalModelGatewayService {

    private final IExternalModelHttpClient httpClient;

    public ExternalModelGatewayService() {
        this(null);
    }

    public ExternalModelGatewayService(IExternalModelHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ExternalModelGatewayResponseDTO complete(String provider,
                                                    String baseUrl,
                                                    ExternalModelGatewayExecutionPolicyDTO executionPolicy,
                                                    boolean networkEnabled,
                                                    ExternalModelGatewayRequestDTO request) {
        requireText(provider, "provider");
        requireText(baseUrl, "baseUrl");
        requireExecutionPolicy(executionPolicy);
        if (request == null) {
            throw new IllegalArgumentException("external model gateway request is required");
        }
        requireText(request.getModel(), "model");
        requireText(request.getInput(), "input");

        if (networkEnabled) {
            if (httpClient != null) {
                return httpClient.complete(provider, baseUrl, executionPolicy, toOpenAiRequest(request));
            }
            return ExternalModelGatewayResponseDTO.builder()
                    .answer("external model network execution is enabled but HTTP adapter is not implemented: "
                            + provider + "/" + request.getModel() + " @ " + baseUrl
                            + " timeoutMs=" + executionPolicy.getTimeoutMs()
                            + " retryAttempts=" + executionPolicy.getRetryAttempts())
                    .build();
        }

        return ExternalModelGatewayResponseDTO.builder()
                .answer("external model gateway is configured but network execution is disabled: "
                        + provider + "/" + request.getModel() + " @ " + baseUrl
                        + " timeoutMs=" + executionPolicy.getTimeoutMs()
                        + " retryAttempts=" + executionPolicy.getRetryAttempts())
                .build();
    }

    private OpenAiChatCompletionRequestDTO toOpenAiRequest(ExternalModelGatewayRequestDTO request) {
        return OpenAiChatCompletionRequestDTO.builder()
                .model(request.getModel())
                .messages(List.of(
                        OpenAiChatCompletionMessageDTO.builder()
                                .role("system")
                                .content("")
                                .build(),
                        OpenAiChatCompletionMessageDTO.builder()
                                .role("user")
                                .content(request.getInput())
                                .build()))
                .tools(toOpenAiTools(request))
                .build();
    }

    private List<OpenAiChatCompletionToolDTO> toOpenAiTools(ExternalModelGatewayRequestDTO request) {
        if (request.getTools() == null) {
            return List.of();
        }
        return request.getTools().stream()
                .map(this::toOpenAiTool)
                .collect(Collectors.toList());
    }

    private OpenAiChatCompletionToolDTO toOpenAiTool(ExternalModelGatewayToolDTO tool) {
        return OpenAiChatCompletionToolDTO.builder()
                .type("function")
                .function(OpenAiChatCompletionToolFunctionDTO.builder()
                        .name(tool.getName())
                        .description(tool.getDescription())
                        .parameters("{\"type\":\"object\",\"additionalProperties\":true}")
                        .build())
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
