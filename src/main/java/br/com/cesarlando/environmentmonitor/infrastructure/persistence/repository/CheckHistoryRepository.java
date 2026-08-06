package br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository;

import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.CheckHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckHistoryRepository extends JpaRepository<CheckHistoryEntity, Long> {
}
