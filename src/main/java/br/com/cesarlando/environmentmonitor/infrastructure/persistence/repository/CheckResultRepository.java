package br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository;

import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.CheckResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckResultRepository extends JpaRepository<CheckResultEntity, Long> {
}
