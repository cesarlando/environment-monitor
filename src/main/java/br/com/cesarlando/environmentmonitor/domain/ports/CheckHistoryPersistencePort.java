package br.com.cesarlando.environmentmonitor.domain.ports;

import br.com.cesarlando.environmentmonitor.domain.model.CheckHistory;

import java.util.List;

public interface CheckHistoryPersistencePort {

    CheckHistory save(CheckHistory checkHistory);
    List<CheckHistory> findByEnvironmentId(Long environmentId);
    List<CheckHistory> findLatestByEnvironmentId(Long environmentId);
}
