package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayToolDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayExecutionPolicyDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ExternalModelGatewayServiceTest {

    @Test
    public void shouldReturnDisabledResponseWithoutSecretValuesOrNetworkCall() {
        ExternalModelGatewayService service = new ExternalModelGatewayService();

        ExternalModelGatewayResponseDTO response = service.complete("openai", "https://api.xiaomimimo.com/v1/chat/completions", policy(30000, 0), ExternalModelGatewayRequestDTO.builder()
                .model("gpt-5.4")
                .conversationId("conv-gateway-service")
                .input("hello")
                .tools(List.of(tool("file_read")))
                .build());

        Assert.assertTrue(response.getAnswer().contains("external model gateway is configured but network execution is disabled"));
        Assert.assertTrue(response.getAnswer().contains("openai/gpt-5.4"));
        Assert.assertTrue(response.getAnswer().contains("https://api.xiaomimimo.com/v1/chat/completions"));
        Assert.assertTrue(response.getAnswer().contains("timeoutMs=30000"));
        Assert.assertTrue(response.getAnswer().contains("retryAttempts=0"));
        Assert.assertFalse(response.toString().contains("OPENAI_API_KEY"));
        Assert.assertFalse(response.toString().contains("sk-"));
    }

    @Test
    public void shouldRejectBlankProviderBeforeGatewayExecution() {
        ExternalModelGatewayService service = new ExternalModelGatewayService();

        try {
            service.complete(" ", "https://api.xiaomimimo.com/v1/chat/completions", policy(30000, 0), validRequest());
            Assert.fail("Expected blank provider to be rejected.");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("provider"));
            Assert.assertFalse(exception.getMessage().contains("OPENAI_API_KEY"));
            Assert.assertFalse(exception.getMessage().contains("sk-"));
        }
    }

    @Test
    public void shouldRejectBlankModelBeforeGatewayExecution() {
        ExternalModelGatewayService service = new ExternalModelGatewayService();

        try {
            service.complete("openai", "https://api.xiaomimimo.com/v1/chat/completions", policy(30000, 0), ExternalModelGatewayRequestDTO.builder()
                    .model(" ")
                    .conversationId("conv-gateway-service")
                    .input("hello")
                    .tools(List.of(tool("file_read")))
                    .build());
            Assert.fail("Expected blank model to be rejected.");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("model"));
            Assert.assertFalse(exception.getMessage().contains("OPENAI_API_KEY"));
            Assert.assertFalse(exception.getMessage().contains("sk-"));
        }
    }

    @Test
    public void shouldRejectBlankInputBeforeGatewayExecution() {
        ExternalModelGatewayService service = new ExternalModelGatewayService();

        try {
            service.complete("openai", "https://api.xiaomimimo.com/v1/chat/completions", policy(30000, 0), ExternalModelGatewayRequestDTO.builder()
                    .model("gpt-5.4")
                    .conversationId("conv-gateway-service")
                    .input(" ")
                    .tools(List.of(tool("file_read")))
                    .build());
            Assert.fail("Expected blank input to be rejected.");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("input"));
            Assert.assertFalse(exception.getMessage().contains("OPENAI_API_KEY"));
            Assert.assertFalse(exception.getMessage().contains("sk-"));
        }
    }

    @Test
    public void shouldRejectBlankBaseUrlBeforeGatewayExecution() {
        ExternalModelGatewayService service = new ExternalModelGatewayService();

        try {
            service.complete("openai", " ", policy(30000, 0), validRequest());
            Assert.fail("Expected blank base URL to be rejected.");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("baseUrl"));
            Assert.assertFalse(exception.getMessage().contains("OPENAI_API_KEY"));
            Assert.assertFalse(exception.getMessage().contains("sk-"));
        }
    }

    @Test
    public void shouldRejectInvalidExecutionPolicyBeforeGatewayExecution() {
        ExternalModelGatewayService service = new ExternalModelGatewayService();

        try {
            service.complete("openai", "https://api.xiaomimimo.com/v1/chat/completions", policy(0, -1), validRequest());
            Assert.fail("Expected invalid execution policy to be rejected.");
        } catch (IllegalArgumentException exception) {
            Assert.assertTrue(exception.getMessage().contains("execution policy"));
            Assert.assertFalse(exception.getMessage().contains("OPENAI_API_KEY"));
            Assert.assertFalse(exception.getMessage().contains("sk-"));
        }
    }

    private ExternalModelGatewayRequestDTO validRequest() {
        return ExternalModelGatewayRequestDTO.builder()
                .model("gpt-5.4")
                .conversationId("conv-gateway-service")
                .input("hello")
                .tools(List.of(tool("file_read")))
                .build();
    }

    private ExternalModelGatewayToolDTO tool(String name) {
        return ExternalModelGatewayToolDTO.builder()
                .name(name)
                .description("Read a workspace file")
                .defaultPermissionBehavior("allow")
                .build();
    }

    private ExternalModelGatewayExecutionPolicyDTO policy(int timeoutMs, int retryAttempts) {
        return ExternalModelGatewayExecutionPolicyDTO.builder()
                .timeoutMs(timeoutMs)
                .retryAttempts(retryAttempts)
                .build();
    }

}
