package com.digitalemployee.cases.conversation;

import com.digitalemployee.cases.IConversationCaseService;
import com.digitalemployee.domain.conversation.adapter.port.IModelDecisionPort;
import com.digitalemployee.domain.conversation.adapter.port.IShellCommandPort;
import com.digitalemployee.domain.conversation.adapter.port.IWorkspaceFilePort;
import com.digitalemployee.domain.conversation.adapter.port.IWorkspaceFileWritePort;
import com.digitalemployee.domain.conversation.adapter.repository.IConversationTurnRepository;
import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;
import com.digitalemployee.domain.conversation.service.ConversationRuntimeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationCaseService implements IConversationCaseService {

    private final ConversationRuntimeService conversationRuntimeService;
    private final IConversationTurnRepository conversationTurnRepository;

    public ConversationCaseService(IWorkspaceFilePort workspaceFilePort,
                                   IWorkspaceFileWritePort workspaceFileWritePort,
                                   IShellCommandPort shellCommandPort,
                                   IModelDecisionPort modelDecisionPort,
                                   IConversationTurnRepository conversationTurnRepository) {
        this.conversationRuntimeService = new ConversationRuntimeService(
                workspaceFilePort,
                workspaceFileWritePort,
                shellCommandPort,
                modelDecisionPort,
                this::loadMemoryContext
        );
        this.conversationTurnRepository = conversationTurnRepository;
    }

    @Override
    public ConversationTurnAggregate submitMessage(String conversationId, String message) {
        ConversationTurnAggregate turn = conversationRuntimeService.handleUserMessage(conversationId, message);
        conversationTurnRepository.save(turn);
        return turn;
    }

    @Override
    public List<ConversationMessageEntity> listMessages(String conversationId) {
        return conversationTurnRepository.listMessages(conversationId);
    }

    private String loadMemoryContext(String conversationId) {
        List<ConversationMessageEntity> history = conversationTurnRepository.listMessages(conversationId);
        if (history.isEmpty()) {
            return "<memory_context>\n</memory_context>";
        }
        String conversationHistory = history.stream()
                .map(message -> message.getRole().getCode() + ": " + abbreviate(message.getContent(), 500))
                .collect(Collectors.joining("\n"));
        return "<memory_context>\n"
                + "<conversation_history>\n"
                + conversationHistory
                + "\n</conversation_history>\n"
                + "</memory_context>";
    }

    private String abbreviate(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...[truncated]";
    }

}
