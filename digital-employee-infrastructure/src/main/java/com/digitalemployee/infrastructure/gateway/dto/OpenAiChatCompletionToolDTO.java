package com.digitalemployee.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OpenAiChatCompletionToolDTO implements Serializable {

    private static final long serialVersionUID = 6362456578720441855L;

    private String type;
    private OpenAiChatCompletionToolFunctionDTO function;

}
