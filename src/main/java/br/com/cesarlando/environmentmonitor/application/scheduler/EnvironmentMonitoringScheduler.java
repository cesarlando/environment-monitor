package br.com.cesarlando.environmentmonitor.application.scheduler;

import br.com.cesarlando.environmentmonitor.application.usecase.CheckEnvironmentUseCase;
import br.com.cesarlando.environmentmonitor.application.usecase.LoadEnvironmentUseCase;
import br.com.cesarlando.environmentmonitor.application.usecase.SaveCheckHistoryUseCase;
import br.com.cesarlando.environmentmonitor.application.usecase.SaveCheckResultUseCase;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnvironmentMonitoringScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(EnvironmentMonitoringScheduler.class);

    private final CheckEnvironmentUseCase checkEnvironmentUseCase;
    private final LoadEnvironmentUseCase loadEnvironmentUseCase;
    private final SaveCheckResultUseCase saveCheckResultUseCase;

    private final SaveCheckHistoryUseCase saveCheckHistoryUseCase;

    public EnvironmentMonitoringScheduler(CheckEnvironmentUseCase checkEnvironmentUseCase, LoadEnvironmentUseCase loadEnvironmentUseCase, SaveCheckResultUseCase saveCheckResultUseCase, SaveCheckHistoryUseCase saveCheckHistoryUseCase) {
        this.checkEnvironmentUseCase = checkEnvironmentUseCase;
        this.loadEnvironmentUseCase = loadEnvironmentUseCase;
        this.saveCheckResultUseCase = saveCheckResultUseCase;
        this.saveCheckHistoryUseCase = saveCheckHistoryUseCase;
    }

    @Scheduled(
            fixedDelayString = "${monitor.scheduler.fixed-delay}",
            initialDelayString = "${monitor.scheduler.initial-delay}"
        )

    public void execute() {

        logger.info("Executando ciclo de monitoramento");

        List<Environment> environments =
                loadEnvironmentUseCase.execute();

        logger.info(
                "Ambientes encontrados para monitoramento: {}",
                environments.size()
        );

        for (Environment environment : environments) {

            try {
                CheckResult result =
                        checkEnvironmentUseCase.execute(environment);

                CheckResult savedResult =
                        saveCheckResultUseCase.execute(result);
                saveCheckHistoryUseCase.execute(savedResult);

                logger.info(
                        "Resultado do ciclo: {}",
                        savedResult
                );
            } catch(Exception exception) {
                logger.error("Falha ao processar o ambiente {}: {}", environment.getName(), exception.getMessage(), exception);
            }
        }
    }
}