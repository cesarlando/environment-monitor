package br.com.cesarlando.environmentmonitor.infrastructure.checker.collector;

import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump.HighJumpAuthenticationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CollectorEnvironmentChecker implements EnvironmentChecker {
    private static final Logger logger = LoggerFactory.getLogger(CollectorEnvironmentChecker.class);
    private final LocalEnvironmentProperties localEnvironmentProperties;
    private final HighJumpAuthenticationClient highJumpAuthenticationClient;

    public CollectorEnvironmentChecker(LocalEnvironmentProperties localEnvironmentProperties, HighJumpAuthenticationClient highJumpAuthenticationClient) {
        this.localEnvironmentProperties = localEnvironmentProperties;
        this.highJumpAuthenticationClient = highJumpAuthenticationClient;
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

        String loginUrl = highJumpAuthenticationClient.buildLoginUrl(config.getEndpoint());

        String jsonPayload = highJumpAuthenticationClient.buildLoginPayload(config);

        try {

            int responseCode =
                    highJumpAuthenticationClient.authenticate(
                            loginUrl,
                            jsonPayload
                    );

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
        }
    }
}
