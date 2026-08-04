package br.com.cesarlando.environmentmonitor.controller.mapper;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentStatusResponse;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentStatusResponseMapper {

    public EnvironmentStatusResponse toResponse(CheckResult checkResult) {

        EnvironmentStatusResponse response = new EnvironmentStatusResponse();

        response.setId(checkResult.getEnvironment().getId());
        response.setName(checkResult.getEnvironment().getName());
        response.setType(checkResult.getEnvironment().getType().name());
        response.setEndpoint(checkResult.getEnvironment().getEndpoint());
        response.setStatus(checkResult.getStatus().name());
        response.setResponseTime(checkResult.getResponseTime());
        response.setCheckedAt(checkResult.getCheckedAt());
        response.setDetails(checkResult.getDetails());

        return response;
    }

}
