package br.com.cesarlando.environmentmonitor.domain.ports;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;

import java.util.Optional;

public interface CheckResultPersistencePort {
    CheckResult save(CheckResult checkResult);

    Optional<CheckResult> findLatestByEnvironmentId(Long environmentId);

}
