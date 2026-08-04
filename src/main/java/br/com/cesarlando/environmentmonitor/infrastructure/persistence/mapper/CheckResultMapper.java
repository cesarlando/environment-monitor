package br.com.cesarlando.environmentmonitor.infrastructure.persistence.mapper;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.CheckResultEntity;
import org.springframework.stereotype.Component;

@Component
public class CheckResultMapper {
    private final EnvironmentMapper environmentMapper;

    public CheckResultMapper(EnvironmentMapper environmentMapper) {
        this.environmentMapper = environmentMapper;
    }

    public CheckResult toDomain(CheckResultEntity entity) {
        CheckResult checkResult = new CheckResult();

        checkResult.setEnvironment(environmentMapper.toDomain(entity.getEnvironment()));
        checkResult.setStatus(entity.getStatus());
        checkResult.setCheckedAt(entity.getCheckedAt());
        checkResult.setResponseTime(entity.getResponseTime());
        checkResult.setDetails(entity.getDetails());

        return checkResult;
    }

    public CheckResultEntity toEntity(CheckResult checkResult) {

        CheckResultEntity entity = new CheckResultEntity();

        entity.setEnvironment(environmentMapper.toEntity(checkResult.getEnvironment()));
        entity.setStatus(checkResult.getStatus());
        entity.setCheckedAt(checkResult.getCheckedAt());
        entity.setResponseTime(checkResult.getResponseTime());
        entity.setDetails(checkResult.getDetails());

        return entity;
    }
}
