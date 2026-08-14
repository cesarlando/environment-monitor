package br.com.cesarlando.environmentmonitor.domain.ports;

import br.com.cesarlando.environmentmonitor.domain.model.Environment;

public interface NotificationPort {

    void notifyStatusChange(
            Environment environment,
            String previousStatus,
            String currentStatus,
            String details);
}

