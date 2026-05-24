package com.digitalemployee.domain.memory.model.entity;

import com.digitalemployee.domain.memory.model.valobj.MemoryScopeVO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemoryEntryEntity {

    private final MemoryScopeVO scope;
    private final String title;
    private final String content;

}
