package com.digitalemployee.domain.conversation.model.valobj;

public enum PermissionBehaviorVO {

    ALLOW("allow"),
    DENY("deny"),
    ASK("ask");

    private final String code;

    PermissionBehaviorVO(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
