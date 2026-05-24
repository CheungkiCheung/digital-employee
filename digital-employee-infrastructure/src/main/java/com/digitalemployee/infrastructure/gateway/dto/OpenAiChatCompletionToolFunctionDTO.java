package com.digitalemployee.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class OpenAiChatCompletionToolFunctionDTO implements Serializable {

    private static final long serialVersionUID = 4412110481789648786L;

    private String name;
    private String description;
    private String parameters;

}
