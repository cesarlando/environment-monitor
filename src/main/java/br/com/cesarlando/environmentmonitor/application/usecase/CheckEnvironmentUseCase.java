package br.com.cesarlando.environmentmonitor.application.usecase;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.application.factory.EnvironmentCheckerFactory;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import org.springframework.stereotype.Component;

@Component
public class CheckEnvironmentUseCase {
    private final EnvironmentCheckerFactory environmentCheckerFactory;

    public CheckEnvironmentUseCase (EnvironmentCheckerFactory environmentCheckerFactory) {
        this.environmentCheckerFactory = environmentCheckerFactory;
    }

    public CheckResult execute (Environment environment) {
        EnvironmentChecker checker = environmentCheckerFactory.getChecker();

        return checker.check(environment);
    }
}
