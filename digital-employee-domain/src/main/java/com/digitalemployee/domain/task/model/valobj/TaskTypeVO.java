package com.digitalemployee.domain.task.model.valobj;

public enum TaskTypeVO {

    LOCAL_BASH("b"),
    LOCAL_AGENT("a"),
    REMOTE_AGENT("r"),
    IN_PROCESS_TEAMMATE("t"),
    LOCAL_WORKFLOW("w"),
    MONITOR_MCP("m"),
    DREAM("d");

    private final String idPrefix;

    TaskTypeVO(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

}
