package com.digitalemployee.domain.conversation.model.entity;

import com.digitalemployee.domain.conversation.model.valobj.ConversationMessageRoleVO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversationMessageEntity {

    private final ConversationMessageRoleVO role;
    private final String content;

}
