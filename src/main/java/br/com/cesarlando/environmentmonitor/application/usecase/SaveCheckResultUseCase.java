package br.com.cesarlando.environmentmonitor.application.usecase;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.ports.CheckResultPersistencePort;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.CheckResultPersistenceAdapter;
import org.springframework.stereotype.Component;

@Component
public class SaveCheckResultUseCase {

    private CheckResultPersistencePort checkResultPersistencePort;

    public SaveCheckResultUseCase(CheckResultPersistencePort checkResultPersistencePort) {
        this.checkResultPersistencePort = checkResultPersistencePort;
    }

    public CheckResult execute(CheckResult checkResult) {
        return checkResultPersistencePort.save(checkResult);
    }
}
