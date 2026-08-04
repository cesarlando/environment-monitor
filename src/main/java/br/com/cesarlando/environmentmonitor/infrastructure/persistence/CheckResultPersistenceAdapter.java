package br.com.cesarlando.environmentmonitor.infrastructure.persistence;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.ports.CheckResultPersistencePort;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.mapper.CheckResultMapper;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository.CheckResultRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CheckResultPersistenceAdapter implements CheckResultPersistencePort {

    private final CheckResultRepository checkResultRepository;
    private final CheckResultMapper checkResultMapper;

    public CheckResultPersistenceAdapter(CheckResultRepository checkResultRepository, CheckResultMapper checkResultMapper) {
        this.checkResultRepository = checkResultRepository;
        this.checkResultMapper = checkResultMapper;
    }

    @Override
    public CheckResult save(CheckResult checkResult) {

        return checkResultMapper.toDomain(checkResultRepository.save(checkResultMapper.toEntity(checkResult)));
    }

    @Override
    public Optional<CheckResult> findLatestByEnvironmentId(Long environmentId) {
        return checkResultRepository
                .findTopByEnvironmentIdOrderByCheckedAtDesc(environmentId)
                .map(checkResultMapper::toDomain);
    }
}
