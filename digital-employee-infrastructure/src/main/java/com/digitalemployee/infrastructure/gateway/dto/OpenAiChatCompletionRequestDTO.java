package com.digitalemployee.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class OpenAiChatCompletionRequestDTO implements Serializable {

    private static final long serialVersionUID = 7428224126421199209L;

    private String model;
    private List<OpenAiChatCompletionMessageDTO> messages;
    private List<OpenAiChatCompletionToolDTO> tools;

}
