package com.digitalemployee.domain.conversation.model.valobj;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ModelDecisionRequestVO {

    private final String conversationId;
    private final String userMessage;
    private final String memoryContext;
    private final List<ToolDescriptorVO> availableTools;

}
