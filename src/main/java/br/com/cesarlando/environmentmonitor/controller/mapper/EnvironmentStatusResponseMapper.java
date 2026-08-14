package br.com.cesarlando.environmentmonitor.controller.mapper;

import br.com.cesarlando.environmentmonitor.config.database.DatabaseConfigurationRepository;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentStatusResponse;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentStatusResponseMapper {

    private static final String DATABASE_PREFIX = "database://";

    private final DatabaseConfigurationRepository databaseConfigurationRepository;

    public EnvironmentStatusResponseMapper(DatabaseConfigurationRepository databaseConfigurationRepository) {
        this.databaseConfigurationRepository = databaseConfigurationRepository;
    }

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

        if(checkResult.getEnvironment().getType() == EnvironmentType.DATABASE) {
            response.setDatabaseType(getDatabaseType(checkResult.getEnvironment().getEndpoint()));
        }

        return response;
    }

    private String getDatabaseType(String endpoint) {

        if(endpoint == null || !endpoint.startsWith(DATABASE_PREFIX)) {
            return null;
        }
        String databaseKey = endpoint.substring(DATABASE_PREFIX.length());

        return databaseConfigurationRepository
                .findByKey(databaseKey)
                .map(config -> config.getDatabaseType())
                .orElse(null);
    }

}
