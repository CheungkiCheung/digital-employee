package com.digitalemployee.infrastructure.config;

import com.digitalemployee.domain.conversation.adapter.port.IModelDecisionPort;
import com.digitalemployee.infrastructure.adapter.port.DeterministicModelDecisionPort;
import com.digitalemployee.infrastructure.adapter.port.ExternalModelDecisionPort;
import com.digitalemployee.infrastructure.gateway.ExternalModelGatewayMapper;
import com.digitalemployee.infrastructure.gateway.ExternalModelGatewayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelDecisionPortConfiguration {

    private static final String DETERMINISTIC_PROVIDER = "deterministic";

    @Bean
    public IModelDecisionPort modelDecisionPort(
            @Value("${digital-employee.model.provider:deterministic}") String provider,
            @Value("${digital-employee.model.name:local-rules}") String model,
            @Value("${digital-employee.model.api-key-env-name:}") String apiKeyEnvName,
            @Value("${digital-employee.model.base-url:}") String baseUrl,
            @Value("${digital-employee.model.timeout-ms:30000}") int timeoutMs,
            @Value("${digital-employee.model.retry-attempts:0}") int retryAttempts,
            @Value("${digital-employee.model.network-enabled:false}") boolean networkEnabled) {
        if (DETERMINISTIC_PROVIDER.equalsIgnoreCase(provider)) {
            return new DeterministicModelDecisionPort();
        }
        return new ExternalModelDecisionPort(provider, model, apiKeyEnvName, baseUrl,
                timeoutMs, retryAttempts, networkEnabled,
                externalModelGatewayService(), externalModelGatewayMapper());
    }

    @Bean
    public ExternalModelGatewayMapper externalModelGatewayMapper() {
        return new ExternalModelGatewayMapper();
    }

    @Bean
    public ExternalModelGatewayService externalModelGatewayService() {
        return new ExternalModelGatewayService();
    }

}
