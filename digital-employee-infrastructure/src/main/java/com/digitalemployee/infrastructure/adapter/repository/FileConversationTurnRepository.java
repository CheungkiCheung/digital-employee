package com.digitalemployee.infrastructure.adapter.repository;

import com.digitalemployee.domain.conversation.adapter.repository.IConversationTurnRepository;
import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;
import com.digitalemployee.domain.conversation.model.valobj.ConversationMessageRoleVO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class FileConversationTurnRepository implements IConversationTurnRepository {

    private final Path storageDirectory;

    public FileConversationTurnRepository(Path storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    @Override
    public void save(ConversationTurnAggregate turn) {
        try {
            Files.createDirectories(storageDirectory);
            List<String> lines = turn.getMessages().stream()
                    .map(this::serialize)
                    .collect(Collectors.toList());
            Files.write(conversationPath(turn.getConversationId()), lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            throw new IllegalStateException("failed to save conversation history", exception);
        }
    }

    @Override
    public List<ConversationMessageEntity> listMessages(String conversationId) {
        Path conversationPath = conversationPath(conversationId);
        if (!Files.exists(conversationPath)) {
            return List.of();
        }
        try {
            return Files.readAllLines(conversationPath, StandardCharsets.UTF_8).stream()
                    .filter(line -> !line.isBlank())
                    .map(this::deserialize)
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException exception) {
            throw new IllegalStateException("failed to read conversation history", exception);
        }
    }

    private Path conversationPath(String conversationId) {
        String safeConversationId = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(conversationId.getBytes(StandardCharsets.UTF_8));
        return storageDirectory.resolve(safeConversationId + ".tsv");
    }

    private String serialize(ConversationMessageEntity message) {
        String encodedContent = Base64.getEncoder()
                .encodeToString(message.getContent().getBytes(StandardCharsets.UTF_8));
        return message.getRole().name() + "\t" + encodedContent;
    }

    private ConversationMessageEntity deserialize(String line) {
        String[] columns = line.split("\t", 2);
        if (columns.length != 2) {
            throw new IllegalStateException("invalid conversation history record");
        }
        String content = new String(Base64.getDecoder().decode(columns[1]), StandardCharsets.UTF_8);
        return ConversationMessageEntity.builder()
                .role(ConversationMessageRoleVO.valueOf(columns[0]))
                .content(content)
                .build();
    }

}
