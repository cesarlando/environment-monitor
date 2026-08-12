package br.com.cesarlando.environmentmonitor.application.usecase;

import br.com.cesarlando.environmentmonitor.domain.model.CheckHistory;
import br.com.cesarlando.environmentmonitor.domain.ports.CheckHistoryPersistencePort;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentHistorySummaryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadEnvironmentHistorySummaryUseCase {

    private final CheckHistoryPersistencePort checkHistoryPersistencePort;

    public LoadEnvironmentHistorySummaryUseCase(CheckHistoryPersistencePort checkHistoryPersistencePort) {
        this.checkHistoryPersistencePort = checkHistoryPersistencePort;
    }

    public EnvironmentHistorySummaryResponse execute(Long environmentId) {

        List<CheckHistory> history = checkHistoryPersistencePort.findByEnvironmentId(environmentId);

        EnvironmentHistorySummaryResponse response = new EnvironmentHistorySummaryResponse();

        response.setEnvironmentId(environmentId);

        if(history.isEmpty()) {
            response.setTotalChecks(0);
            response.setOffLineCount(0);
            response.setAverageResponseTime(0.0);

            return response;
        }
        response.setEnvironmentName(history.getFirst().getEnvironmentName());
        response.setTotalChecks(history.size());
        long offLineCount = history.stream().filter(item -> "OFFLINE".equals(item.getStatus()))
                .count();
        response.setOffLineCount(offLineCount);

        double averageResponseTime = history.stream()
                .filter(item -> item.getResponseTime() != null)
                .mapToLong(CheckHistory::getResponseTime)
                .average()
                .orElse(0.0);

        response.setAverageResponseTime(averageResponseTime);

        return response;
    }
}
