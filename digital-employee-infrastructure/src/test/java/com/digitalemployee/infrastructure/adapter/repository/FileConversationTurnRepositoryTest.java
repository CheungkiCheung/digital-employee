package com.digitalemployee.infrastructure.adapter.repository;

import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;
import com.digitalemployee.domain.conversation.model.valobj.ConversationMessageRoleVO;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileConversationTurnRepositoryTest {

    @Test
    public void shouldPersistAndReloadConversationMessagesByConversationId() throws Exception {
        Path storageDirectory = Files.createTempDirectory("digital-employee-conversation-history");
        FileConversationTurnRepository firstRepository = new FileConversationTurnRepository(storageDirectory);

        firstRepository.save(ConversationTurnAggregate.builder()
                .conversationId("conv-file")
                .answer("hello")
                .messages(List.of(
                        ConversationMessageEntity.builder()
                                .role(ConversationMessageRoleVO.USER)
                                .content("hello")
                                .build(),
                        ConversationMessageEntity.builder()
                                .role(ConversationMessageRoleVO.ASSISTANT)
                                .content("hi from disk")
                                .build()))
                .toolExecutions(List.of())
                .build());

        FileConversationTurnRepository secondRepository = new FileConversationTurnRepository(storageDirectory);

        List<ConversationMessageEntity> messages = secondRepository.listMessages("conv-file");
        Assert.assertEquals(2, messages.size());
        Assert.assertEquals(ConversationMessageRoleVO.USER, messages.get(0).getRole());
        Assert.assertEquals("hello", messages.get(0).getContent());
        Assert.assertEquals(ConversationMessageRoleVO.ASSISTANT, messages.get(1).getRole());
        Assert.assertEquals("hi from disk", messages.get(1).getContent());
        Assert.assertTrue(secondRepository.listMessages("other-conversation").isEmpty());
    }

}
