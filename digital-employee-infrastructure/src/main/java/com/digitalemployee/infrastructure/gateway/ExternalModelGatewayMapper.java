package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO;
import com.digitalemployee.domain.conversation.model.valobj.ToolDescriptorVO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayToolDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayToolList;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionMessageDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionToolDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionToolFunctionDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExternalModelGatewayMapper {

    public ExternalModelGatewayRequestDTO toGatewayRequest(String model, ModelDecisionRequestVO request) {
        List<ExternalModelGatewayToolDTO> tools = request.getAvailableTools() == null
                ? Collections.emptyList()
                : request.getAvailableTools().stream()
                .map(this::toGatewayTool)
                .collect(Collectors.toList());

        return ExternalModelGatewayRequestDTO.builder()
                .model(model)
                .conversationId(request.getConversationId())
                .input(buildInput(request))
                .tools(new ExternalModelGatewayToolList(tools))
                .build();
    }

    public String toDirectAnswer(ExternalModelGatewayResponseDTO response) {
        return response.getAnswer();
    }

    public OpenAiChatCompletionRequestDTO toOpenAiChatCompletionRequest(String model, ModelDecisionRequestVO request) {
        List<OpenAiChatCompletionToolDTO> tools = request.getAvailableTools() == null
                ? Collections.emptyList()
                : request.getAvailableTools().stream()
                .map(this::toOpenAiTool)
                .collect(Collectors.toList());

        return OpenAiChatCompletionRequestDTO.builder()
                .model(model)
                .messages(List.of(
                        OpenAiChatCompletionMessageDTO.builder()
                                .role("system")
                                .content(request.getMemoryContext() == null ? "" : request.getMemoryContext())
                                .build(),
                        OpenAiChatCompletionMessageDTO.builder()
                                .role("user")
                                .content(request.getUserMessage() == null ? "" : request.getUserMessage())
                                .build()))
                .tools(tools)
                .build();
    }

    private String buildInput(ModelDecisionRequestVO request) {
        String memoryContext = request.getMemoryContext() == null ? "" : request.getMemoryContext();
        String userMessage = request.getUserMessage() == null ? "" : request.getUserMessage();
        return memoryContext + "\n\nUser: " + userMessage;
    }

    private ExternalModelGatewayToolDTO toGatewayTool(ToolDescriptorVO tool) {
        return ExternalModelGatewayToolDTO.builder()
                .name(tool.getName())
                .description(tool.getDescription())
                .defaultPermissionBehavior(tool.getDefaultPermissionBehavior().getCode())
                .build();
    }

    private OpenAiChatCompletionToolDTO toOpenAiTool(ToolDescriptorVO tool) {
        return OpenAiChatCompletionToolDTO.builder()
                .type("function")
                .function(OpenAiChatCompletionToolFunctionDTO.builder()
                        .name(tool.getName())
                        .description(tool.getDescription())
                        .parameters("{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\"}},\"additionalProperties\":true}")
                        .build())
                .build();
    }

}
