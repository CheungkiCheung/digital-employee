package com.digitalemployee.domain.memory.service;

import com.digitalemployee.domain.memory.model.entity.MemoryEntryEntity;

import java.util.List;
import java.util.stream.Collectors;

public class MemoryContextDomainService {

    public String buildContextBlock(List<MemoryEntryEntity> entries, int maxCharacters) {
        if (entries == null || entries.isEmpty()) {
            return "<memory_context>\n</memory_context>";
        }
        String body = entries.stream()
                .map(this::formatEntry)
                .collect(Collectors.joining("\n"));
        String block = "<memory_context>\n" + body + "\n</memory_context>";
        if (block.length() <= maxCharacters) {
            return block;
        }
        int closingLength = "\n</memory_context>".length();
        int bodyLimit = Math.max(0, maxCharacters - "<memory_context>\n".length() - closingLength);
        String boundedBody = body.length() <= bodyLimit ? body : body.substring(0, bodyLimit);
        return "<memory_context>\n" + boundedBody + "\n</memory_context>";
    }

    private String formatEntry(MemoryEntryEntity entry) {
        return "[" + entry.getScope().name() + "] " + entry.getTitle() + ": " + entry.getContent();
    }

}
