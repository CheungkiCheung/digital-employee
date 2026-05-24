package com.digitalemployee.domain.conversation.adapter.repository;

import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;

import java.util.List;

public interface IConversationTurnRepository {

    void save(ConversationTurnAggregate turn);

    List<ConversationMessageEntity> listMessages(String conversationId);

}
