package br.com.cesarlando.environmentmonitor.infrastructure.persistence.mapper;

import br.com.cesarlando.environmentmonitor.domain.model.CheckHistory;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.CheckHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CheckHistoryMapper {

    public CheckHistory toDomain(CheckHistoryEntity entity) {

        CheckHistory history = new CheckHistory();

        history.setId(entity.getId());
        history.setEnvironmentId(entity.getEnvironmentId());
        history.setEnvironmentName(entity.getEnvironmentName());
        history.setEnvironmentType(entity.getEnvironmentType());
        history.setStatus(entity.getStatus());
        history.setResponseTime(entity.getResponseTime());
        history.setDetails(entity.getDetails());
        history.setCheckedAt(entity.getCheckedAt());

        return history;
    }

    public CheckHistoryEntity toEntity(CheckHistory history) {

        CheckHistoryEntity entity = new CheckHistoryEntity();

        entity.setId(history.getId());
        entity.setEnvironmentId(history.getEnvironmentId());
        entity.setEnvironmentName(history.getEnvironmentName());
        entity.setEnvironmentType(history.getEnvironmentType());
        entity.setStatus(history.getStatus());
        entity.setResponseTime(history.getResponseTime());
        entity.setDetails(history.getDetails());
        entity.setCheckedAt(history.getCheckedAt());

        return entity;
    }
}
