package br.com.cesarlando.environmentmonitor.domain.ports;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;

public interface CheckResultPersistencePort {
    CheckResult save(CheckResult checkResult);
}
