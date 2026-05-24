package com.digitalemployee.infrastructure.config;

import com.digitalemployee.domain.conversation.adapter.port.IModelDecisionPort;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionVO;
import com.digitalemployee.infrastructure.adapter.port.DeterministicModelDecisionPort;
import com.digitalemployee.infrastructure.adapter.port.ExternalModelDecisionPort;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ModelDecisionPortSelectionTest {

    @Test
    public void shouldUseDeterministicProviderByDefault() {
        ModelDecisionPortConfiguration configuration = new ModelDecisionPortConfiguration();

        IModelDecisionPort port = configuration.modelDecisionPort("deterministic", "local-rules", "", "", 30000, 0);

        Assert.assertTrue(port instanceof DeterministicModelDecisionPort);
        Assert.assertEquals("deterministic", port.provider().getProvider());
        Assert.assertFalse(port.provider().isExternal());
    }

    @Test
    public void shouldUseExternalProviderWhenConfigured() {
        ModelDecisionPortConfiguration configuration = new ModelDecisionPortConfiguration();

        IModelDecisionPort port = configuration.modelDecisionPort(
                "openai",
                "gpt-5.4",
                "OPENAI_API_KEY",
                "https://api.xiaomimimo.com/v1/chat/completions",
                30000,
                0);

        Assert.assertTrue(port instanceof ExternalModelDecisionPort);
        Assert.assertEquals("openai", port.provider().getProvider());
        Assert.assertEquals("gpt-5.4", port.provider().getModel());
        Assert.assertTrue(port.provider().isExternal());
        Assert.assertEquals("OPENAI_API_KEY", port.provider().getApiKeyEnvName());
    }

    @Test
    public void shouldPassConfiguredExecutionPolicyToExternalProvider() {
        ModelDecisionPortConfiguration configuration = new ModelDecisionPortConfiguration();

        IModelDecisionPort port = configuration.modelDecisionPort(
                "openai",
                "gpt-5.4",
                "OPENAI_API_KEY",
                "https://api.xiaomimimo.com/v1/chat/completions",
                45000,
                2);

        ModelDecisionVO decision = port.decideNextAction(ModelDecisionRequestVO.builder()
                .conversationId("conv-policy-config")
                .userMessage("hello")
                .memoryContext("<memory_context>\n</memory_context>")
                .availableTools(List.of())
                .build());

        Assert.assertTrue(decision.getDirectAnswer().contains("timeoutMs=45000"));
        Assert.assertTrue(decision.getDirectAnswer().contains("retryAttempts=2"));
    }

}
