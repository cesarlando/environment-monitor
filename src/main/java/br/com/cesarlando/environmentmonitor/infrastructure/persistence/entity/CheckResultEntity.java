package br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "check_results")
public class CheckResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "environment_id", nullable = false)
    private EnvironmentEntity environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnvironmentStatus status;

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    @Column(name = "response_time")
    private Long responseTime;

    @Column(length = 500)
    private String details;

    public CheckResultEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnvironmentEntity getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentEntity environment) {
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
