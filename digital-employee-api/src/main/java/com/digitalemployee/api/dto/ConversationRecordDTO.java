package com.digitalemployee.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ConversationRecordDTO implements Serializable {

    private static final long serialVersionUID = -141561981709055343L;

    private String role;
    private String content;

}
