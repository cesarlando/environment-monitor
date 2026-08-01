package br.com.cesarlando.environmentmonitor.domain.model;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;

public class Environment {

    private Long id;
    private String name;
    private EnvironmentType type;
    private String endpoint;
    private EnvironmentStatus status;

    public Environment () {

    }
}
