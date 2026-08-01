package br.com.cesarlando.environmentmonitor.domain.model;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;

import java.time.LocalDateTime;

public class CheckResult {
    private Environment environment;
    private EnvironmentStatus status;
    private LocalDateTime checkedAt;
    private Long responseTime;

    public CheckResult () {

    }
}
