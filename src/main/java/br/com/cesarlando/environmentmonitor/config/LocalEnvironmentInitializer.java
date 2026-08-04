package br.com.cesarlando.environmentmonitor.config;

import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.EnvironmentEntity;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository.CheckResultRepository;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository.EnvironmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalEnvironmentInitializer implements CommandLineRunner {

    private final LocalEnvironmentProperties localEnvironmentProperties;
    private final EnvironmentRepository environmentRepository;

    private final CheckResultRepository checkResultRepository;

    public LocalEnvironmentInitializer(LocalEnvironmentProperties localEnvironmentProperties, EnvironmentRepository environmentRepository, CheckResultRepository checkResultRepository) {
        this.localEnvironmentProperties = localEnvironmentProperties;
        this.environmentRepository = environmentRepository;
        this.checkResultRepository = checkResultRepository;
    }

    @Override
    public void run(String... args) {

        checkResultRepository.deleteAll();
        environmentRepository.deleteAll();

        localEnvironmentProperties
                .getEnvironments()
                .values()
                .forEach(environmentConfig -> {

                    EnvironmentEntity entity = new EnvironmentEntity();

                    entity.setName(environmentConfig.getName());
                    entity.setType(environmentConfig.getType());
                    entity.setEndpoint(environmentConfig.getEndpoint());
                    entity.setStatus(EnvironmentStatus.UNKNOWN);

                    environmentRepository.save(entity);
                });
    }
}