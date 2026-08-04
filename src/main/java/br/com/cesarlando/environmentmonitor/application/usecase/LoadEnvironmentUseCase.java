package br.com.cesarlando.environmentmonitor.application.usecase;

import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentPersistencePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadEnvironmentUseCase {

    private final EnvironmentPersistencePort environmentPersistencePort;

    public LoadEnvironmentUseCase(
            EnvironmentPersistencePort environmentPersistencePort) {
        this.environmentPersistencePort = environmentPersistencePort;
    }

    public List<Environment> execute() {
        return environmentPersistencePort.findAll();
    }
}