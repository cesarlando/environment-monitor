package br.com.cesarlando.environmentmonitor.application.factory;

import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.checker.http.HttpEnvironmentChecker;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentCheckerFactory {
    private final HttpEnvironmentChecker httpEnvironmentChecker;

    public EnvironmentCheckerFactory (HttpEnvironmentChecker httpEnvironmentChecker) {
        this.httpEnvironmentChecker = httpEnvironmentChecker;
    }

    public EnvironmentChecker getChecker () {
        return httpEnvironmentChecker;
    }
}
