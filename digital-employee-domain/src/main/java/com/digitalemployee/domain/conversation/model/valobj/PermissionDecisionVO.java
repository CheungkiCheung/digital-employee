package com.digitalemployee.domain.conversation.model.valobj;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionDecisionVO {

    private final PermissionBehaviorVO behavior;
    private final String reason;

}
