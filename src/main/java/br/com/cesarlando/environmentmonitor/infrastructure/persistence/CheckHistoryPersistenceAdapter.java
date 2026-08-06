package br.com.cesarlando.environmentmonitor.infrastructure.persistence;

import br.com.cesarlando.environmentmonitor.domain.model.CheckHistory;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.CheckHistoryEntity;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.mapper.CheckHistoryMapper;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository.CheckHistoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CheckHistoryPersistenceAdapter {

    public final CheckHistoryRepository checkHistoryRepository;
    public final CheckHistoryMapper checkHistoryMapper;

    public CheckHistoryPersistenceAdapter(CheckHistoryRepository checkHistoryRepository, CheckHistoryMapper checkHistoryMapper) {
        this.checkHistoryRepository = checkHistoryRepository;
        this.checkHistoryMapper = checkHistoryMapper;
    }

    public CheckHistory save(CheckHistory checkHistory) {

        CheckHistoryEntity entity = checkHistoryMapper.toEntity(checkHistory);

        CheckHistoryEntity savedEntity = checkHistoryRepository.save(entity);

        return checkHistoryMapper.toDomain(savedEntity);
    }
}
