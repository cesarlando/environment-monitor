package br.com.cesarlando.environmentmonitor.infrastructure.persistence;

import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentPersistencePort;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.mapper.EnvironmentMapper;
import br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository.EnvironmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnvironmentPersistenceAdapter implements EnvironmentPersistencePort {
    private final EnvironmentRepository environmentRepository;
    private final EnvironmentMapper environmentMapper;

    public EnvironmentPersistenceAdapter(EnvironmentRepository environmentRepository, EnvironmentMapper environmentMapper) {
        this.environmentRepository = environmentRepository;
        this.environmentMapper = environmentMapper;
    }

    @Override
    public List<Environment> findAll() {
        return environmentRepository.findAll()
                .stream()
                .map(environmentMapper::toDomain)
                .toList();
    }
}
