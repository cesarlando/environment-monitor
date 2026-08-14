package br.com.cesarlando.environmentmonitor.infrastructure.persistence.repository;

import br.com.cesarlando.environmentmonitor.infrastructure.persistence.entity.CheckHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckHistoryRepository extends JpaRepository<CheckHistoryEntity, Long> {

    List<CheckHistoryEntity> findByEnvironmentIdOrderByCheckedAtDesc(Long environmentId);
    List<CheckHistoryEntity> findTop50ByEnvironmentIdOrderByCheckedAtDesc(Long environmentId);
}
