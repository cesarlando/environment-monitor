package br.com.cesarlando.environmentmonitor.domain.model;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;

import java.time.LocalDateTime;

public class CheckResult {
    private Environment environment;
    private EnvironmentStatus status;
    private LocalDateTime checkedAt;
    private Long responseTime;
    private String details;

    public CheckResult () {

    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public EnvironmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnvironmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
