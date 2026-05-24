package com.digitalemployee.infrastructure.adapter.repository;

import com.digitalemployee.domain.conversation.adapter.repository.IConversationTurnRepository;
import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryConversationTurnRepository implements IConversationTurnRepository {

    private final Map<String, List<ConversationMessageEntity>> messagesByConversationId = new ConcurrentHashMap<>();

    @Override
    public void save(ConversationTurnAggregate turn) {
        messagesByConversationId.compute(turn.getConversationId(), (conversationId, existingMessages) -> {
            List<ConversationMessageEntity> updatedMessages = new ArrayList<>();
            if (existingMessages != null) {
                updatedMessages.addAll(existingMessages);
            }
            updatedMessages.addAll(turn.getMessages());
            return Collections.unmodifiableList(updatedMessages);
        });
    }

    @Override
    public List<ConversationMessageEntity> listMessages(String conversationId) {
        return List.copyOf(messagesByConversationId.getOrDefault(conversationId, List.of()));
    }

}
