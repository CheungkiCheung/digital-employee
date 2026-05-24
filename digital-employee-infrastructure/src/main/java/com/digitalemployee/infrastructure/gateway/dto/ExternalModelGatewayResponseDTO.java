package com.digitalemployee.infrastructure.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ExternalModelGatewayResponseDTO implements Serializable {

    private static final long serialVersionUID = -3809718216093915814L;

    private String answer;

}
