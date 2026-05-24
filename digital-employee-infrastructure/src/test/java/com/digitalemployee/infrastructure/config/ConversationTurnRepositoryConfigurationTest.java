package com.digitalemployee.infrastructure.config;

import com.digitalemployee.domain.conversation.adapter.repository.IConversationTurnRepository;
import com.digitalemployee.infrastructure.adapter.repository.FileConversationTurnRepository;
import com.digitalemployee.infrastructure.adapter.repository.InMemoryConversationTurnRepository;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConversationTurnRepositoryConfigurationTest {

    @Test
    public void shouldUseInMemoryRepositoryByDefault() {
        ConversationTurnRepositoryConfiguration configuration = new ConversationTurnRepositoryConfiguration();

        IConversationTurnRepository repository = configuration.conversationTurnRepository("memory", "target/test-conversation-history");

        Assert.assertTrue(repository instanceof InMemoryConversationTurnRepository);
    }

    @Test
    public void shouldUseFileRepositoryWhenConfigured() throws Exception {
        ConversationTurnRepositoryConfiguration configuration = new ConversationTurnRepositoryConfiguration();
        Path storageDirectory = Files.createTempDirectory("digital-employee-configured-history");

        IConversationTurnRepository repository = configuration.conversationTurnRepository("file", storageDirectory.toString());

        Assert.assertTrue(repository instanceof FileConversationTurnRepository);
    }

}
