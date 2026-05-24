package com.digitalemployee.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ConversationMessageResponseDTO implements Serializable {

    private static final long serialVersionUID = 7586019042858043887L;

    private String conversationId;
    private String answer;
    private List<ConversationRecordDTO> messages;
    private List<ToolExecutionDTO> toolExecutions;

}
