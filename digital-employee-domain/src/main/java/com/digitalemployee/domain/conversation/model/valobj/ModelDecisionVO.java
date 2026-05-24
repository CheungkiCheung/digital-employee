package com.digitalemployee.domain.conversation.model.valobj;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ModelDecisionVO {

    private final ModelDecisionTypeVO type;
    private final String toolName;
    private final String toolInput;
    private final String directAnswer;

    public boolean isToolCall(String expectedToolName) {
        return type == ModelDecisionTypeVO.TOOL_CALL && expectedToolName.equals(toolName);
    }

}
