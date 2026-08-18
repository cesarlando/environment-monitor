package br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump;

import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class HighJumpAuthenticationClient {

    private final ObjectMapper objectMapper;

    public HighJumpAuthenticationClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String buildLoginUrl(String endpoint) {

        URI uri = URI.create(endpoint);

        String path = uri.getPath();

        if (path == null || path.isBlank() || "/".equals(path)) {

            return endpoint.replaceAll("/$", "") + "/odata/LogOn";
        }
        return endpoint.replaceAll("/$", "") + "/LogOn";
    }

    public String buildWebLoginUrl(
            String endpoint,
            String authenticationPath) {

        URI uri = URI.create(endpoint);

        String baseUrl =
                uri.getScheme()
                        + "://"
                        + uri.getHost()
                        + (uri.getPort() != -1
                        ? ":" + uri.getPort()
                        : "");

        return baseUrl + authenticationPath;
    }

    public String buildLoginPayload(LocalEnvironmentProperties.EnvironmentConfig config) {
        Map<String, Object> payload = Map.of(
                "applicationId", "HighJump One Platform",
                "connectionType", "platform",
                "tenant", config.getTenant() != null
                        ? config.getTenant()
                        : "",
                "userLogOnName", config.getUsername(),
                "userPassword", config.getPassword()
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Erro ao gerar JSON de autenticação HighJump",
                    exception
            );
        }
    }

    public HighJumpAuthenticationResult authenticate(
            String loginUrl,
            String jsonPayload) {

        HttpURLConnection connection = null;

        try {

            URL url = new URL(loginUrl);

            connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );
            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            try (OutputStreamWriter writer =
                         new OutputStreamWriter(connection.getOutputStream())) {

                writer.write(jsonPayload);
                writer.flush();
            }

            int responseCode = connection.getResponseCode();

            InputStream responseStream =
                    responseCode >= 400
                            ? connection.getErrorStream()
                            : connection.getInputStream();

            String responseBody = "";

            if (responseStream != null) {
                responseBody =
                        new String(
                                responseStream.readAllBytes(),
                                StandardCharsets.UTF_8
                        );
            }

            return new HighJumpAuthenticationResult(
                    responseCode,
                    responseBody
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Erro ao executar autenticação HighJump",
                    exception
            );

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public record HighJumpAuthenticationResult(
            int responseCode,
            String responseBody
    ) {
    }

    public String extractSerializedAuthenticationTicket(String responseBody) {

        try {
            var root = objectMapper.readTree(responseBody);

            var ticketNode = root.get("SerializedAuthenticationTicket");

            if (ticketNode == null || ticketNode.isNull()) {
                throw new IllegalStateException(
                        "SerializedAuthenticationTicket não encontrado na resposta HighJump"
                );
            }

            return ticketNode.asText();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Erro ao extrair SerializedAuthenticationTicket da resposta HighJump",
                    exception
            );
        }
    }
}
