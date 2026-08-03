package br.com.cesarlando.environmentmonitor.application.scheduler;

import br.com.cesarlando.environmentmonitor.application.usecase.CheckEnvironmentUseCase;
import br.com.cesarlando.environmentmonitor.application.usecase.LoadEnvironmentsUseCase;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnvironmentMonitoringScheduler {
    private final CheckEnvironmentUseCase checkEnvironmentUseCase;
    private final LoadEnvironmentsUseCase loadEnvironmentsUseCase;
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentMonitoringScheduler.class);

    public EnvironmentMonitoringScheduler(CheckEnvironmentUseCase checkEnvironmentUseCase, LoadEnvironmentsUseCase loadEnvironmentsUseCase) {
        this.checkEnvironmentUseCase = checkEnvironmentUseCase;
        this.loadEnvironmentsUseCase = loadEnvironmentsUseCase;
    }

    @Scheduled(fixedDelayString = "${monitor.scheduler.fixed-delay}")
    public void execute () {
        logger.info("Executando Ciclo de Monitoramento");

        List<Environment> environments = loadEnvironmentsUseCase.execute();

        logger.info("Ambientes encontrados para monitoramento: {}", environments.size());

        for (Environment environment : environments) {
            CheckResult result = checkEnvironmentUseCase.execute(environment);

            logger.info("Resultado do ciclo: {}", result);
        }
    }

}
