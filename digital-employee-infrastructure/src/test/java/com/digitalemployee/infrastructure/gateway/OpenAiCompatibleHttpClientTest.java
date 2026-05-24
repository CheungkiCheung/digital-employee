package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayExecutionPolicyDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionMessageDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionToolDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionToolFunctionDTO;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class OpenAiCompatibleHttpClientTest {

    private HttpServer server;
    private AtomicReference<String> method;
    private AtomicReference<String> path;
    private AtomicReference<String> authorizationHeader;
    private AtomicReference<String> requestBody;

    @Before
    public void setUp() throws IOException {
        method = new AtomicReference<>();
        path = new AtomicReference<>();
        authorizationHeader = new AtomicReference<>();
        requestBody = new AtomicReference<>();

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/chat/completions", this::handleCompletion);
        server.start();
    }

    @After
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void shouldPostOpenAiCompatibleRequestToFakeServerAndExtractAnswer() {
        OpenAiCompatibleHttpClient client = new OpenAiCompatibleHttpClient(() -> "test-api-key");

        ExternalModelGatewayResponseDTO response = client.complete(
                "openai",
                "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/chat/completions",
                ExternalModelGatewayExecutionPolicyDTO.builder()
                        .timeoutMs(30000)
                        .retryAttempts(0)
                        .build(),
                OpenAiChatCompletionRequestDTO.builder()
                        .model("gpt-5.4")
                        .messages(List.of(
                                OpenAiChatCompletionMessageDTO.builder()
                                        .role("system")
                                        .content("memory")
                                        .build(),
                                OpenAiChatCompletionMessageDTO.builder()
                                        .role("user")
                                        .content("hello")
                                        .build()))
                        .tools(List.of(OpenAiChatCompletionToolDTO.builder()
                                .type("function")
                                .function(OpenAiChatCompletionToolFunctionDTO.builder()
                                        .name("file_read")
                                        .description("Read a workspace file")
                                        .parameters("{\"type\":\"object\"}")
                                        .build())
                                .build()))
                        .build());

        Assert.assertEquals("fake HTTP model answer", response.getAnswer());
        Assert.assertEquals("POST", method.get());
        Assert.assertEquals("/v1/chat/completions", path.get());
        Assert.assertEquals("Bearer test-api-key", authorizationHeader.get());
        Assert.assertTrue(requestBody.get().contains("\"model\":\"gpt-5.4\""));
        Assert.assertTrue(requestBody.get().contains("\"role\":\"user\""));
        Assert.assertTrue(requestBody.get().contains("\"content\":\"hello\""));
        Assert.assertTrue(requestBody.get().contains("\"name\":\"file_read\""));
        Assert.assertFalse(response.toString().contains("test-api-key"));
    }

    private void handleCompletion(HttpExchange exchange) throws IOException {
        method.set(exchange.getRequestMethod());
        path.set(exchange.getRequestURI().getPath());
        authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
        requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));

        byte[] response = "{\"choices\":[{\"message\":{\"content\":\"fake HTTP model answer\"}}]}".getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

}
