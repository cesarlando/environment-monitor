package br.com.cesarlando.environmentmonitor.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PublicKey;

@ConfigurationProperties(prefix = "notification.teams")
public class TeamsNotificationProperties {

    private String webhookUrl;

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
}
