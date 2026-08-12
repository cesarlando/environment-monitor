package br.com.cesarlando.environmentmonitor.infrastructure.notification;

import br.com.cesarlando.environmentmonitor.config.properties.TeamsNotificationProperties;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class TeamsNotificationAdapter implements NotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(TeamsNotificationAdapter.class);
    private final RestClient restClient;
    private final TeamsNotificationProperties properties;

    public TeamsNotificationAdapter(
            RestClient restClient,
            TeamsNotificationProperties properties) {

        this.restClient = restClient;
        this.properties = properties;

        logger.info(
                "Teams webhook configurado: {}",
                properties.getWebhookUrl() != null
                        && !properties.getWebhookUrl().isBlank()
        );
    }

    @Override
    public void notifyStatusChange(
            Environment environment,
            String previousStatus,
            String currentStatus,
            String details) {
        try {

            Map<String, Object> payload =
                    buildPayload(
                            environment,
                            previousStatus,
                            currentStatus,
                            details
                    );

            logger.info(
                    "Enviando alerta para Teams | Ambiente: {} | {} -> {}",
                    environment.getName(),
                    previousStatus,
                    currentStatus
            );

            String webhookUrl = properties.getWebhookUrl().trim();

            String json = new ObjectMapper()
                    .writeValueAsString(payload);

            restClient
                    .post()
                    .uri(URI.create(webhookUrl))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json.getBytes(StandardCharsets.UTF_8))
                    .retrieve()
                    .toBodilessEntity();

            logger.info(
                    "Alerta enviado para Teams com sucesso | Ambiente: {}",
                    environment.getName()
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Falha ao enviar alerta para o Teams",
                    exception
            );
        }
    }

    private Map<String, Object> buildPayload(
            Environment environment,
            String previousStatus,
            String currentStatus,
            String details) {

        String title =
                "OFFLINE".equals(currentStatus)
                        ? "🚨 Ambiente indisponível"
                        : "✅ Ambiente normalizado";

        Map<String, Object> content = Map.of(
                "$schema", "http://adaptivecards.io/schemas/adaptive-card.json",
                "type", "AdaptiveCard",
                "version", "1.4",
                "body", List.of(
                        Map.of(
                                "type", "TextBlock",
                                "text", title,
                                "weight", "Bolder",
                                "size", "Large",
                                "wrap", true
                        ),
                        Map.of(
                                "type", "TextBlock",
                                "text", "Ambiente: " + environment.getName(),
                                "wrap", true
                        ),
                        Map.of(
                                "type", "TextBlock",
                                "text", "Tipo: " + environment.getType().name(),
                                "wrap", true
                        ),
                        Map.of(
                                "type", "TextBlock",
                                "text",
                                "Status: "
                                        + formatStatus(previousStatus)
                                        + " → "
                                        + formatStatus(currentStatus),
                                "wrap", true
                        ),
                        Map.of(
                                "type", "TextBlock",
                                "text", "Detalhes: " + details,
                                "wrap", true
                        )
                )
        );

        Map<String, Object> attachment = new HashMap<>();
        attachment.put(
                "contentType",
                "application/vnd.microsoft.card.adaptive"
        );
        attachment.put("contentUrl", null);
        attachment.put("content", content);

        return Map.of(
                "type", "message",
                "attachments", List.of(attachment)
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
