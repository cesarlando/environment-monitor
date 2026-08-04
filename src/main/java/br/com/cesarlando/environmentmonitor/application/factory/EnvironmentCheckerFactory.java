package br.com.cesarlando.environmentmonitor.application.factory;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
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

    public EnvironmentChecker getChecker(Environment environment) {

        EnvironmentType type = environment.getType();

        return  switch (type) {
            case WEB, COLLECTOR -> httpEnvironmentChecker;
            case DATABASE -> throw new IllegalArgumentException("Database checker not implemented yet");
            case MIDDLEWARE -> throw new IllegalArgumentException("Middleware checker not implemented yet");
        };
    }
}
