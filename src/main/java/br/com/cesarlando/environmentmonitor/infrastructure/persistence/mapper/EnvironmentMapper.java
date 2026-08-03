package br.com.cesarlando.environmentmonitor.infrastructure.persistence.mapper;

import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.EnvironmentEntity;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentMapper {
    public Environment toDomain(EnvironmentEntity entity) {
        Environment environment = new Environment();

        environment.setId(entity.getId());
        environment.setName(entity.getName());
        environment.setType(entity.getType());
        environment.setEndpoint(entity.getEndpoint());
        environment.setStatus(entity.getStatus());

        return environment;
    }

    public EnvironmentEntity toEntity(Environment environment) {
        EnvironmentEntity entity = new EnvironmentEntity();

        entity.setId(environment.getId());
        entity.setName(environment.getName());
        entity.setType(environment.getType());
        entity.setEndpoint(environment.getEndpoint());
        entity.setStatus(environment.getStatus());

        return entity;
    }
}
