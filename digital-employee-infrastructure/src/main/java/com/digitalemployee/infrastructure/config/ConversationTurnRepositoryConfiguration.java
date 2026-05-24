package com.digitalemployee.infrastructure.config;

import com.digitalemployee.domain.conversation.adapter.repository.IConversationTurnRepository;
import com.digitalemployee.infrastructure.adapter.repository.FileConversationTurnRepository;
import com.digitalemployee.infrastructure.adapter.repository.InMemoryConversationTurnRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class ConversationTurnRepositoryConfiguration {

    private static final String FILE_REPOSITORY = "file";

    @Bean
    public IConversationTurnRepository conversationTurnRepository(
            @Value("${digital-employee.conversation.repository:memory}") String repositoryType,
            @Value("${digital-employee.conversation.file-storage-path:target/conversation-history}") String fileStoragePath) {
        if (FILE_REPOSITORY.equalsIgnoreCase(repositoryType)) {
            return new FileConversationTurnRepository(Path.of(fileStoragePath));
        }
        return new InMemoryConversationTurnRepository();
    }

}
