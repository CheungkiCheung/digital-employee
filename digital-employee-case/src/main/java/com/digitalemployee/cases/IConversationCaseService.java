package com.digitalemployee.cases;

import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;

import java.util.List;

public interface IConversationCaseService {

    ConversationTurnAggregate submitMessage(String conversationId, String message);

    List<ConversationMessageEntity> listMessages(String conversationId);

}
