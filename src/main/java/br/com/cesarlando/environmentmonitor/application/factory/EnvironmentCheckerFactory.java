package br.com.cesarlando.environmentmonitor.application.factory;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.checker.database.DatabaseEnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.checker.http.HttpEnvironmentChecker;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentCheckerFactory {
    private final HttpEnvironmentChecker httpEnvironmentChecker;
    private final DatabaseEnvironmentChecker databaseEnvironmentChecker;

    public EnvironmentCheckerFactory(HttpEnvironmentChecker httpEnvironmentChecker, DatabaseEnvironmentChecker databaseEnvironmentChecker) {
        this.httpEnvironmentChecker = httpEnvironmentChecker;
        this.databaseEnvironmentChecker = databaseEnvironmentChecker;
    }
    public EnvironmentChecker getChecker(Environment environment) {

        EnvironmentType type = environment.getType();

        return  switch (type) {
            case WEB, COLLECTOR -> httpEnvironmentChecker;
            case DATABASE -> databaseEnvironmentChecker;
            case MIDDLEWARE -> throw new IllegalArgumentException("Middleware checker not implemented yet");
        };
    }
}
