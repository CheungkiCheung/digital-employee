package com.digitalemployee.domain.conversation.model.valobj;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkspaceFileVO {

    private final String path;
    private final String content;

    public String preview(int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength);
    }

}
