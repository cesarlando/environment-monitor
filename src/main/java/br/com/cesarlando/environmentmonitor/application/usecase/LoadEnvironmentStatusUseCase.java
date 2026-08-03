package br.com.cesarlando.environmentmonitor.application.usecase;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.CheckResultPersistencePort;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentPersistencePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadEnvironmentStatusUseCase {

    private final EnvironmentPersistencePort environmentPersistencePort;
    private final CheckResultPersistencePort checkResultPersistencePort;

    public LoadEnvironmentStatusUseCase(
            EnvironmentPersistencePort environmentPersistencePort,
            CheckResultPersistencePort checkResultPersistencePort) {

        this.environmentPersistencePort = environmentPersistencePort;
        this.checkResultPersistencePort = checkResultPersistencePort;
    }

    public List<CheckResult> execute() {

        List<Environment> environments =
                environmentPersistencePort.findAll();

        return environments.stream()
                .map(environment ->
                        checkResultPersistencePort
                                .findLatestByEnvironmentId(environment.getId())
                                .orElseGet(() -> createUnknownResult(environment))
                )
                .toList();
    }

    private CheckResult createUnknownResult(Environment environment) {

        CheckResult checkResult = new CheckResult();

        checkResult.setEnvironment(environment);
        checkResult.setStatus(EnvironmentStatus.UNKNOWN);
        checkResult.setDetails("No monitoring result available");

        return checkResult;
    }
}