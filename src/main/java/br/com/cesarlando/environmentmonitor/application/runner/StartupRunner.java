package br.com.cesarlando.environmentmonitor.application.runner;

import br.com.cesarlando.environmentmonitor.application.usecase.CheckEnvironmentUseCase;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class StartupRunner implements CommandLineRunner {

    private final CheckEnvironmentUseCase checkEnvironmentUseCase;
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    public StartupRunner(CheckEnvironmentUseCase checkEnvironmentUseCase) {
        this.checkEnvironmentUseCase = checkEnvironmentUseCase;
    }

    @Override
    public void run(String... args) throws Exception {

        Environment environment = new Environment();

        environment.setName("Google");
        environment.setType(EnvironmentType.WEB);
        environment.setEndpoint("https://www.google.com");

        CheckResult result = checkEnvironmentUseCase.execute(environment);

        logger.info("Resultado do monitoramento: {}", result);

    }

}
