package br.com.cesarlando.environmentmonitor.application.usecase;

import br.com.cesarlando.environmentmonitor.domain.model.CheckHistory;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.CheckHistoryPersistenceAdapter;
import org.springframework.stereotype.Component;

@Component
public class SaveCheckHistoryUseCase {

    private final CheckHistoryPersistenceAdapter checkHistoryPersistenceAdapter;

    public SaveCheckHistoryUseCase(CheckHistoryPersistenceAdapter checkHistoryPersistenceAdapter) {
        this.checkHistoryPersistenceAdapter = checkHistoryPersistenceAdapter;
    }

    public CheckHistory execute(CheckResult checkResult) {

        CheckHistory history = new CheckHistory();

        history.setEnvironmentId(checkResult.getEnvironment().getId());
        history.setEnvironmentName(checkResult.getEnvironment().getName());
        history.setEnvironmentType(checkResult.getEnvironment().getType().name());
        history.setStatus(checkResult.getStatus().name());
        history.setResponseTime(checkResult.getResponseTime());
        history.setDetails(checkResult.getDetails());
        history.setCheckedAt(checkResult.getCheckedAt());

        return checkHistoryPersistenceAdapter.save(history);
    }
}
