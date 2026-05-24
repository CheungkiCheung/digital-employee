package com.digitalemployee.infrastructure.adapter.port;

import com.digitalemployee.domain.conversation.adapter.port.IModelDecisionPort;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionTypeVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelProviderVO;
import com.digitalemployee.infrastructure.gateway.ExternalModelGatewayMapper;
import com.digitalemployee.infrastructure.gateway.ExternalModelGatewayService;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayExecutionPolicyDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayRequestDTO;
import com.digitalemployee.infrastructure.gateway.dto.ExternalModelGatewayResponseDTO;

public class ExternalModelDecisionPort implements IModelDecisionPort {

    private final String provider;
    private final String model;
    private final String apiKeyEnvName;
    private final String baseUrl;
    private final int timeoutMs;
    private final int retryAttempts;
    private final ExternalModelGatewayService gatewayService;
    private final ExternalModelGatewayMapper gatewayMapper;

    public ExternalModelDecisionPort(String provider, String model, String apiKeyEnvName, String baseUrl,
                                     int timeoutMs, int retryAttempts,
                                     ExternalModelGatewayService gatewayService,
                                     ExternalModelGatewayMapper gatewayMapper) {
        this.provider = provider;
        this.model = model;
        this.apiKeyEnvName = apiKeyEnvName;
        this.baseUrl = baseUrl;
        this.timeoutMs = timeoutMs;
        this.retryAttempts = retryAttempts;
        this.gatewayService = gatewayService;
        this.gatewayMapper = gatewayMapper;
    }

    @Override
    public ModelProviderVO provider() {
        return ModelProviderVO.builder()
                .provider(provider)
                .model(model)
                .external(true)
                .apiKeyEnvName(apiKeyEnvName)
                .build();
    }

    @Override
    public ModelDecisionVO decideNextAction(ModelDecisionRequestVO request) {
        ExternalModelGatewayRequestDTO gatewayRequest = gatewayMapper.toGatewayRequest(model, request);
        ExternalModelGatewayResponseDTO gatewayResponse = gatewayService.complete(provider, baseUrl, defaultExecutionPolicy(), gatewayRequest);
        return ModelDecisionVO.builder()
                .type(ModelDecisionTypeVO.DIRECT_RESPONSE)
                .directAnswer(gatewayMapper.toDirectAnswer(gatewayResponse))
                .build();
    }

    private ExternalModelGatewayExecutionPolicyDTO defaultExecutionPolicy() {
        return ExternalModelGatewayExecutionPolicyDTO.builder()
                .timeoutMs(timeoutMs)
                .retryAttempts(retryAttempts)
                .build();
    }

}
