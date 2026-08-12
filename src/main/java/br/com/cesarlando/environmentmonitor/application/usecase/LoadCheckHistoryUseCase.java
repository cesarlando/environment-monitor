package br.com.cesarlando.environmentmonitor.application.usecase;

import br.com.cesarlando.environmentmonitor.domain.model.CheckHistory;
import br.com.cesarlando.environmentmonitor.domain.ports.CheckHistoryPersistencePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadCheckHistoryUseCase {

    private final CheckHistoryPersistencePort checkHistoryPersistencePort;

    public LoadCheckHistoryUseCase(CheckHistoryPersistencePort checkHistoryPersistencePort) {
        this.checkHistoryPersistencePort = checkHistoryPersistencePort;
    }

    public List<CheckHistory> execute(Long environmentId) {

        return checkHistoryPersistencePort.findLatestByEnvironmentId(environmentId);
    }
}
