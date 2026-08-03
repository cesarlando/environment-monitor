package br.com.cesarlando.environmentmonitor.domain.ports;

import br.com.cesarlando.environmentmonitor.domain.model.Environment;

import java.util.List;

public interface EnvironmentPersistencePort {

    List<Environment> findAll();
}
