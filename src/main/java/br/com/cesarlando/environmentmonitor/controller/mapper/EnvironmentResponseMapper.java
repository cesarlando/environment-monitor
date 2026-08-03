package br.com.cesarlando.environmentmonitor.controller.mapper;

import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentResponse;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentResponseMapper {

    public EnvironmentResponse toResponse(Environment environment) {

        EnvironmentResponse response = new EnvironmentResponse();
        response.setId(environment.getId());
        response.setName(environment.getName());
        response.setType(environment.getType().name());
        response.setEndpoint(environment.getEndpoint());
        response.setStatus(environment.getStatus().name());

        return response;
    }

}
