package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayExecutionPolicyDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionMessageDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionToolDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OpenAiCompatibleHttpClient implements IExternalModelHttpClient {

    private final HttpClient httpClient;
    private final Supplier<String> apiKeySupplier;

    public OpenAiCompatibleHttpClient(Supplier<String> apiKeySupplier) {
        this(HttpClient.newHttpClient(), apiKeySupplier);
    }

    public OpenAiCompatibleHttpClient(HttpClient httpClient, Supplier<String> apiKeySupplier) {
        this.httpClient = httpClient;
        this.apiKeySupplier = apiKeySupplier;
    }

    @Override
    public ExternalModelGatewayResponseDTO complete(String provider,
                                                    String baseUrl,
                                                    ExternalModelGatewayExecutionPolicyDTO executionPolicy,
                                                    OpenAiChatCompletionRequestDTO request) {
        requireText(apiKeySupplier.get(), "apiKey");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .timeout(Duration.ofMillis(executionPolicy.getTimeoutMs()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKeySupplier.get())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(request), StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("external model HTTP request failed with status " + response.statusCode());
            }
            return ExternalModelGatewayResponseDTO.builder()
                    .answer(extractContent(response.body()))
                    .build();
        } catch (IOException exception) {
            throw new IllegalStateException("external model HTTP request failed", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("external model HTTP request interrupted", exception);
        }
    }

    private String toJson(OpenAiChatCompletionRequestDTO request) {
        String messages = request.getMessages().stream()
                .map(this::messageToJson)
                .collect(Collectors.joining(","));
        String tools = request.getTools().stream()
                .map(this::toolToJson)
                .collect(Collectors.joining(","));
        return "{\"model\":\"" + escape(request.getModel()) + "\","
                + "\"messages\":[" + messages + "],"
                + "\"tools\":[" + tools + "]}";
    }

    private String messageToJson(OpenAiChatCompletionMessageDTO message) {
        return "{\"role\":\"" + escape(message.getRole()) + "\","
                + "\"content\":\"" + escape(message.getContent()) + "\"}";
    }

    private String toolToJson(OpenAiChatCompletionToolDTO tool) {
        return "{\"type\":\"" + escape(tool.getType()) + "\","
                + "\"function\":{"
                + "\"name\":\"" + escape(tool.getFunction().getName()) + "\","
                + "\"description\":\"" + escape(tool.getFunction().getDescription()) + "\","
                + "\"parameters\":" + tool.getFunction().getParameters()
                + "}}";
    }

    private String extractContent(String responseBody) {
        String marker = "\"content\":\"";
        int start = responseBody.indexOf(marker);
        if (start < 0) {
            throw new IllegalStateException("external model HTTP response content is missing");
        }
        int contentStart = start + marker.length();
        int contentEnd = responseBody.indexOf("\"", contentStart);
        if (contentEnd < 0) {
            throw new IllegalStateException("external model HTTP response content is invalid");
        }
        return responseBody.substring(contentStart, contentEnd);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("external model HTTP " + fieldName + " is required");
        }
    }

}
