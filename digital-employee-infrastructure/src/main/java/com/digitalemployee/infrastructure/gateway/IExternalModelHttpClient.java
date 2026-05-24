package com.digitalemployee.infrastructure.gateway;

import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayExecutionPolicyDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;
import com.digitalemployee.infrastructure.gateway.dto.OpenAiChatCompletionRequestDTO;

public interface IExternalModelHttpClient {

    ExternalModelGatewayResponseDTO complete(String provider,
                                             String baseUrl,
                                             ExternalModelGatewayExecutionPolicyDTO executionPolicy,
                                             OpenAiChatCompletionRequestDTO request);

}
