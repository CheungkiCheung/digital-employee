package com.digitalemployee.infrastructure.adapter.port;

import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelProviderVO;
import com.digitalemployee.infrastructure.gateway.ExternalModelGatewayMapper;
import com.digitalemployee.infrastructure.gateway.ExternalModelGatewayService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ExternalModelDecisionPortTest {

    @Test
    public void shouldExposeExternalProviderMetadataWithoutApiKeyValue() {
        ExternalModelDecisionPort port = new ExternalModelDecisionPort("openai", "gpt-5.4", "OPENAI_API_KEY",
                "https://api.xiaomimimo.com/v1/chat/completions",
                new ExternalModelGatewayService(), new ExternalModelGatewayMapper());

        ModelProviderVO provider = port.provider();

        Assert.assertEquals("openai", provider.getProvider());
        Assert.assertEquals("gpt-5.4", provider.getModel());
        Assert.assertTrue(provider.isExternal());
        Assert.assertEquals("OPENAI_API_KEY", provider.getApiKeyEnvName());
        Assert.assertFalse(provider.toString().contains("sk-"));
    }

    @Test
    public void shouldRouteDecisionThroughGatewayServiceWithoutNetworkCall() {
        ExternalModelDecisionPort port = new ExternalModelDecisionPort("openai", "gpt-5.4", "OPENAI_API_KEY",
                "https://api.xiaomimimo.com/v1/chat/completions",
                new ExternalModelGatewayService(), new ExternalModelGatewayMapper());

        ModelDecisionVO decision = port.decideNextAction(ModelDecisionRequestVO.builder()
                .conversationId("conv-external")
                .userMessage("hello")
                .memoryContext("<memory_context>\n</memory_context>")
                .availableTools(List.of())
                .build());

        Assert.assertTrue(decision.getDirectAnswer().contains("external model gateway is configured but network execution is disabled"));
        Assert.assertTrue(decision.getDirectAnswer().contains("openai/gpt-5.4"));
        Assert.assertTrue(decision.getDirectAnswer().contains("https://api.xiaomimimo.com/v1/chat/completions"));
    }

}
