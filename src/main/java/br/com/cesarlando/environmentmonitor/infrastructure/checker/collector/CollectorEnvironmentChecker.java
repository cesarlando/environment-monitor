package br.com.cesarlando.environmentmonitor.infrastructure.checker.collector;

import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class CollectorEnvironmentChecker implements EnvironmentChecker {

    private static final Logger logger = LoggerFactory.getLogger(CollectorEnvironmentChecker.class);
    private final LocalEnvironmentProperties localEnvironmentProperties;
    private final ObjectMapper objectMapper;

    public CollectorEnvironmentChecker(LocalEnvironmentProperties localEnvironmentProperties, ObjectMapper objectMapper) {
        this.localEnvironmentProperties = localEnvironmentProperties;
        this.objectMapper = objectMapper;
    }

    private LocalEnvironmentProperties.EnvironmentConfig
    findEnvironmentConfig(Environment environment) {
        return localEnvironmentProperties
                .getEnvironments()
                .values()
                .stream()
                .filter(config -> config.getName().equals(environment.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Configuração não encontrada para o ambiente: "
                                + environment.getName()
                ));
    }

    @Override
    public CheckResult check(Environment environment) {

        long startTime = System.currentTimeMillis();

        LocalEnvironmentProperties.EnvironmentConfig config =
                findEnvironmentConfig(environment);

        if (config.getUsername() == null || config.getUsername().isBlank()) {
            throw new IllegalStateException(
                    "Usuário do coletor não configurado: " + environment.getName()
            );
        }

        if (config.getPassword() == null || config.getPassword().isBlank()) {
            throw new IllegalStateException(
                    "Senha do coletor não configurada: " + environment.getName()
            );
        }

        String loginUrl = buildLoginUrl(config.getEndpoint());

        Map<String, Object> payload = Map.of(
                "applicationId","HighJump One Platform",
                "connectionType","platform",
                "tenant", config.getTenant() != null
                        ? config.getTenant()
                        : "",
                "userLogOnName", config.getUsername(),
                "userPassword", config.getPassword()
        );

        logger.info(
                "Executando autenticação do coletor {}",
                environment.getName()
        );

        String jsonPayload;

        try {
            jsonPayload = objectMapper.writeValueAsString(payload);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Erro ao gerar JSON de autenticação do coletor: "
                            + environment.getName(),
                    exception
            );
        }

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

            logger.info(
                    "Resposta autenticação coletor {} | HTTP {}",
                    environment.getName(),
                    responseCode
            );

            long responseTime =
                    System.currentTimeMillis() - startTime;

            if (responseCode == 200) {

                CheckResult checkResult = new CheckResult();

                checkResult.setEnvironment(environment);
                checkResult.setStatus(EnvironmentStatus.ONLINE);
                checkResult.setCheckedAt(LocalDateTime.now());
                checkResult.setResponseTime(responseTime);
                checkResult.setDetails(
                        "Login do coletor realizado com sucesso - HTTP 200"
                );

                return checkResult;
            }

            throw new IllegalStateException(
                    "Falha na autenticação do coletor "
                            + environment.getName()
                            + " - HTTP "
                            + responseCode
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Erro ao autenticar no coletor: "
                            + environment.getName(),
                    exception
            );

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String buildLoginUrl(String endpoint) {

        URI uri = URI.create(endpoint);

        String path = uri.getPath();

        if (path == null || path.isBlank() || "/".equals(path)) {
            return endpoint.replaceAll("/$", "") + "/odata/LogOn";
        }

        return endpoint.replaceAll("/$", "") + "/LogOn";
    }
}
