package br.com.cesarlando.environmentmonitor.application.scheduler;

import br.com.cesarlando.environmentmonitor.application.usecase.CheckEnvironmentUseCase;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentMonitoringScheduler {
    private final CheckEnvironmentUseCase checkEnvironmentUseCase;
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentMonitoringScheduler.class);

    public EnvironmentMonitoringScheduler(CheckEnvironmentUseCase checkEnvironmentUseCase) {
        this.checkEnvironmentUseCase = checkEnvironmentUseCase;
    }

    @Scheduled(fixedDelayString = "${monitor.scheduler.fixed-delay}")
    public void execute () {
        logger.info("Executando Ciclo de Monitoramento");

        Environment environment = new Environment();
        environment.setName("Google");
        environment.setType(EnvironmentType.WEB);
        environment.setEndpoint("https://www.google.com");

        CheckResult result = checkEnvironmentUseCase.execute(environment);

        logger.info("Resultado do ciclo: {}", result);
    }

}
