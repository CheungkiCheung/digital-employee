package com.digitalemployee.domain.conversation.model.valobj;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToolDescriptorVO {

    private final String name;
    private final String description;
    private final PermissionBehaviorVO defaultPermissionBehavior;

}
