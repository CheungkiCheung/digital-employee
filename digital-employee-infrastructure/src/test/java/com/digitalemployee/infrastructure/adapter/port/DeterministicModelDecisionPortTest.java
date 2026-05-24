package com.digitalemployee.infrastructure.adapter.port;

import com.digitalemployee.domain.conversation.model.valobj.ModelProviderVO;
import org.junit.Assert;
import org.junit.Test;

public class DeterministicModelDecisionPortTest {

    @Test
    public void shouldExposeDeterministicProviderMetadata() {
        DeterministicModelDecisionPort port = new DeterministicModelDecisionPort();

        ModelProviderVO provider = port.provider();

        Assert.assertEquals("deterministic", provider.getProvider());
        Assert.assertEquals("local-rules", provider.getModel());
        Assert.assertFalse(provider.isExternal());
        Assert.assertEquals("", provider.getApiKeyEnvName());
    }

    @Test
    public void shouldDescribeExternalProviderWithoutStoringApiKeyValue() {
        ModelProviderVO provider = ModelProviderVO.builder()
                .provider("openai")
                .model("gpt-5.4")
                .external(true)
                .apiKeyEnvName("OPENAI_API_KEY")
                .build();

        Assert.assertTrue(provider.isExternal());
        Assert.assertEquals("OPENAI_API_KEY", provider.getApiKeyEnvName());
        Assert.assertFalse(provider.toString().contains("sk-"));
    }

}
