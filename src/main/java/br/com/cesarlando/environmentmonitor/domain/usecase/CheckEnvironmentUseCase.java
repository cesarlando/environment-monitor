package br.com.cesarlando.environmentmonitor.domain.usecase;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;

public class CheckEnvironmentUseCase {
    private final EnvironmentChecker environmentChecker;

    public CheckEnvironmentUseCase (EnvironmentChecker environmentChecker) {
        this.environmentChecker = environmentChecker;
    }

    public CheckResult execute (Environment environment) {
        return environmentChecker.check(environment);
    }
}
