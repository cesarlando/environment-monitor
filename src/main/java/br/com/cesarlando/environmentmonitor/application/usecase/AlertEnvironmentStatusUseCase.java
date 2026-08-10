package br.com.cesarlando.environmentmonitor.application.usecase;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.ports.CheckResultPersistencePort;
import br.com.cesarlando.environmentmonitor.domain.ports.NotificationPort;
import org.springframework.stereotype.Component;

@Component
public class AlertEnvironmentStatusUseCase {

    private final CheckResultPersistencePort checkResultPersistencePort;
    private final NotificationPort notificationPort;

    public AlertEnvironmentStatusUseCase(CheckResultPersistencePort checkResultPersistencePort, NotificationPort notificationPort) {
        this.checkResultPersistencePort = checkResultPersistencePort;
        this.notificationPort = notificationPort;
    }

    public void execute(CheckResult currentResult) {

        Long environmentId = currentResult.getEnvironment().getId();

        checkResultPersistencePort
                .findLatestByEnvironmentId(environmentId)
                .ifPresent(previousResult -> {
                    String previousStatus = previousResult.getStatus().name();

                    String currentStatus = currentResult.getStatus().name();

                    if (!previousStatus.equals(currentStatus)) {
                        notificationPort.notifyStatusChange(
                                currentResult.getEnvironment(),
                                previousStatus,
                                currentStatus,
                                currentResult.getDetails()
                        );
                    }
                });
    }
}
