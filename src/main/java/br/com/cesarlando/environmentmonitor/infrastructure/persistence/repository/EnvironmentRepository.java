package br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository;

import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.EnvironmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvironmentRepository extends JpaRepository<EnvironmentEntity, Long> {

}
