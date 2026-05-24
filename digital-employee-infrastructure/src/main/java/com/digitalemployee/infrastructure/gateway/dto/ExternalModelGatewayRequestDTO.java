package com.digitalemployee.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ExternalModelGatewayRequestDTO implements Serializable {

    private static final long serialVersionUID = 1744809173173591718L;

    private String model;
    private String conversationId;
    private String input;
    private List<ExternalModelGatewayToolDTO> tools;

}
