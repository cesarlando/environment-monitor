package br.com.cesarlando.environmentmonitor.application.factory;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.checker.collector.CollectorEnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.checker.database.DatabaseEnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.checker.http.HttpEnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.checker.web.WebEnvironmentChecker;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentCheckerFactory {
    private final HttpEnvironmentChecker httpEnvironmentChecker;
    private final DatabaseEnvironmentChecker databaseEnvironmentChecker;
    private final CollectorEnvironmentChecker collectorEnvironmentChecker;

    private final WebEnvironmentChecker webEnvironmentChecker;

    public EnvironmentCheckerFactory(HttpEnvironmentChecker httpEnvironmentChecker, DatabaseEnvironmentChecker databaseEnvironmentChecker, CollectorEnvironmentChecker collectorEnvironmentChecker, WebEnvironmentChecker webEnvironmentChecker) {
        this.httpEnvironmentChecker = httpEnvironmentChecker;
        this.databaseEnvironmentChecker = databaseEnvironmentChecker;
        this.collectorEnvironmentChecker = collectorEnvironmentChecker;
        this.webEnvironmentChecker = webEnvironmentChecker;
    }

    public EnvironmentChecker getChecker(Environment environment) {

        EnvironmentType type = environment.getType();

        return  switch (type) {
            case WEB -> webEnvironmentChecker;
            case MIDDLEWARE -> httpEnvironmentChecker;
            case COLLECTOR -> collectorEnvironmentChecker;
            case DATABASE -> databaseEnvironmentChecker;
        };
    }
}
