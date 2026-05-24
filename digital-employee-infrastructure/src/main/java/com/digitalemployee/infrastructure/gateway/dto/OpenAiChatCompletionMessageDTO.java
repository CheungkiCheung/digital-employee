package com.digitalemployee.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OpenAiChatCompletionMessageDTO implements Serializable {

    private static final long serialVersionUID = 5789881736539333970L;

    private String role;
    private String content;

}
