package com.digitalemployee.domain.conversation.model.aggregate;

import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;
import com.digitalemployee.domain.conversation.model.entity.ToolExecutionEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ConversationTurnAggregate {

    private final String conversationId;
    private final String answer;
    private final List<ConversationMessageEntity> messages;
    private final List<ToolExecutionEntity> toolExecutions;

}
