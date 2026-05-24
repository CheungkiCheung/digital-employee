package com.digitalemployee.domain.conversation.model.entity;

import com.digitalemployee.domain.conversation.model.valobj.PermissionDecisionVO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToolExecutionEntity {

    private final String toolName;
    private final String input;
    private final PermissionDecisionVO permissionDecision;
    private final String result;

}
