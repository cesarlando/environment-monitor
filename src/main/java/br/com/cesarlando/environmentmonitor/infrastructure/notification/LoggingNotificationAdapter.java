package br.com.cesarlando.environmentmonitor.infrastructure.notification;

import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LoggingNotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void notifyStatusChange(
            Environment environment,
            String previousStatus,
            String currentStatus,
            String details) {

        String title =
                "OFFLINE".equals(currentStatus)
                        ? "🚨 Ambiente indisponível"
                        : "✅ Ambiente normalizado";

        String checkedAt =
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        logger.warn(
                """
            
            ==================================================
            {}
            
            Ambiente........: {}
            Tipo............: {}
            Status..........: {} → {}
            Horário.........: {}
            
            Detalhes:
            {}
            ==================================================
            """,
                title,
                environment.getName(),
                environment.getType().name(),
                formatStatus(previousStatus),
                formatStatus(currentStatus),
                checkedAt,
                details
        );
    }

    private String formatStatus(String status) {

        return switch (status) {
            case "ONLINE" -> "🟢 ONLINE";
            case "OFFLINE" -> "🔴 OFFLINE";
            case "WARNING" -> "🟡 WARNING";
            default -> status;
        };
    }
}
