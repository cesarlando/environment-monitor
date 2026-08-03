package br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;
import jakarta.persistence.*;

@Entity
@Table(name = "environments")
public class EnvironmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnvironmentType type;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnvironmentStatus status;

    public EnvironmentEntity () {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnvironmentType getType() {
        return type;
    }

    public void setType(EnvironmentType type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public EnvironmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnvironmentStatus status) {
        this.status = status;
    }
}
