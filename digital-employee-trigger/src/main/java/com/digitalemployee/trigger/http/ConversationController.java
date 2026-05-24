package com.digitalemployee.trigger.http;

import com.digitalemployee.api.dto.ConversationMessageRequestDTO;
import com.digitalemployee.api.dto.ConversationMessageResponseDTO;
import com.digitalemployee.api.dto.ConversationRecordDTO;
import com.digitalemployee.api.dto.ToolExecutionDTO;
import com.digitalemployee.api.response.Response;
import com.digitalemployee.cases.IConversationCaseService;
import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;
import com.digitalemployee.domain.conversation.model.entity.ToolExecutionEntity;
import com.digitalemployee.types.enums.ResponseCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final IConversationCaseService conversationCaseService;

    public ConversationController(IConversationCaseService conversationCaseService) {
        this.conversationCaseService = conversationCaseService;
    }

    @PostMapping("/{conversationId}/messages")
    public Response<ConversationMessageResponseDTO> submitMessage(@PathVariable String conversationId,
                                                                  @RequestBody ConversationMessageRequestDTO request) {
        ConversationTurnAggregate aggregate = conversationCaseService.submitMessage(conversationId, request.getMessage());
        ConversationMessageResponseDTO data = ConversationMessageResponseDTO.builder()
                .conversationId(aggregate.getConversationId())
                .answer(aggregate.getAnswer())
                .messages(toMessageDTOs(aggregate.getMessages()))
                .toolExecutions(toToolDTOs(aggregate.getToolExecutions()))
                .build();
        return Response.<ConversationMessageResponseDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(data)
                .build();
    }

    @GetMapping("/{conversationId}/messages")
    public Response<List<ConversationRecordDTO>> listMessages(@PathVariable String conversationId) {
        return Response.<List<ConversationRecordDTO>>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(toMessageDTOs(conversationCaseService.listMessages(conversationId)))
                .build();
    }

    private List<ConversationRecordDTO> toMessageDTOs(List<ConversationMessageEntity> messages) {
        return messages.stream()
                .map(message -> ConversationRecordDTO.builder()
                        .role(message.getRole().getCode())
                        .content(message.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ToolExecutionDTO> toToolDTOs(List<ToolExecutionEntity> executions) {
        return executions.stream()
                .map(execution -> ToolExecutionDTO.builder()
                        .toolName(execution.getToolName())
                        .input(execution.getInput())
                        .permissionBehavior(execution.getPermissionDecision().getBehavior().getCode())
                        .result(execution.getResult())
                        .build())
                .collect(Collectors.toList());
    }

}
