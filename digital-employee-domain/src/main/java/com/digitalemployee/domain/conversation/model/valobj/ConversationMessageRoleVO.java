package com.digitalemployee.domain.conversation.model.valobj;

public enum ConversationMessageRoleVO {

    USER("user"),
    ASSISTANT("assistant"),
    TOOL_CALL("tool_call"),
    TOOL_RESULT("tool_result");

    private final String code;

    ConversationMessageRoleVO(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
