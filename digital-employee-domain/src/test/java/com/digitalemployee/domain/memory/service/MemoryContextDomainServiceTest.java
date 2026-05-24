package com.digitalemployee.domain.memory.service;

import com.digitalemployee.domain.memory.model.entity.MemoryEntryEntity;
import com.digitalemployee.domain.memory.model.valobj.MemoryScopeVO;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MemoryContextDomainServiceTest {

    @Test
    public void shouldBuildBoundedContextBlockFromMemoryEntries() {
        MemoryContextDomainService service = new MemoryContextDomainService();
        List<MemoryEntryEntity> entries = List.of(
                MemoryEntryEntity.builder()
                        .scope(MemoryScopeVO.PROJECT)
                        .title("Architecture")
                        .content("Use DDD hexagonal boundaries.")
                        .build(),
                MemoryEntryEntity.builder()
                        .scope(MemoryScopeVO.SESSION)
                        .title("Current State")
                        .content("Migrating Claude Code runtime slices.")
                        .build()
        );

        String block = service.buildContextBlock(entries, 180);

        Assert.assertTrue(block.startsWith("<memory_context>"));
        Assert.assertTrue(block.contains("[PROJECT] Architecture: Use DDD hexagonal boundaries."));
        Assert.assertTrue(block.contains("[SESSION] Current State: Migrating Claude Code runtime slices."));
        Assert.assertTrue(block.endsWith("</memory_context>"));
        Assert.assertTrue(block.length() <= 180);
    }

}
