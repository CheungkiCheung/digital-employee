package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO;
import com.digitalemployee.domain.conversation.model.valobj.PermissionBehaviorVO;
import com.digitalemployee.domain.conversation.model.valobj.ToolDescriptorVO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ExternalModelGatewayMapperTest {

    @Test
    public void shouldMapDomainDecisionRequestToGatewayRequestWithoutSecrets() {
        ExternalModelGatewayMapper mapper = new ExternalModelGatewayMapper();
        ModelDecisionRequestVO request = ModelDecisionRequestVO.builder()
                .conversationId("conv-gateway")
                .userMessage("请读取 AGENTS.md")
                .memoryContext("<memory_context>\nrecent context\n</memory_context>")
                .availableTools(List.of(ToolDescriptorVO.builder()
                        .name("file_read")
                        .description("Read a workspace file")
                        .defaultPermissionBehavior(PermissionBehaviorVO.ALLOW)
                        .build()))
                .build();

        ExternalModelGatewayRequestDTO dto = mapper.toGatewayRequest("gpt-5.4", request);

        Assert.assertEquals("gpt-5.4", dto.getModel());
        Assert.assertEquals("conv-gateway", dto.getConversationId());
        Assert.assertTrue(dto.getInput().contains("请读取 AGENTS.md"));
        Assert.assertTrue(dto.getInput().contains("recent context"));
        Assert.assertTrue(dto.getTools().contains("file_read"));
        Assert.assertEquals("file_read", dto.getTools().get(0).getName());
        Assert.assertEquals("Read a workspace file", dto.getTools().get(0).getDescription());
        Assert.assertEquals("allow", dto.getTools().get(0).getDefaultPermissionBehavior());
        Assert.assertFalse(dto.toString().contains("OPENAI_API_KEY"));
        Assert.assertFalse(dto.toString().contains("sk-"));
    }

    @Test
    public void shouldMapGatewayResponseToDirectAnswer() {
        ExternalModelGatewayMapper mapper = new ExternalModelGatewayMapper();
        ExternalModelGatewayResponseDTO response = ExternalModelGatewayResponseDTO.builder()
                .answer("hello from external model")
                .build();

        Assert.assertEquals("hello from external model", mapper.toDirectAnswer(response));
    }

}
