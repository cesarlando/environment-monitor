package br.com.cesarlando.environmentmonitor.infrastructure.checker.collector;

import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump.HighJumpAuthenticationClient;
import br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump.HighJumpSessionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CollectorEnvironmentChecker implements EnvironmentChecker {
    private static final Logger logger = LoggerFactory.getLogger(CollectorEnvironmentChecker.class);
    private final LocalEnvironmentProperties localEnvironmentProperties;
    private final HighJumpAuthenticationClient highJumpAuthenticationClient;
    private final HighJumpSessionClient highJumpSessionClient;

    public CollectorEnvironmentChecker(LocalEnvironmentProperties localEnvironmentProperties, HighJumpAuthenticationClient highJumpAuthenticationClient, HighJumpSessionClient highJumpSessionClient) {
        this.localEnvironmentProperties = localEnvironmentProperties;
        this.highJumpAuthenticationClient = highJumpAuthenticationClient;
        this.highJumpSessionClient = highJumpSessionClient;
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

            HighJumpAuthenticationClient.HighJumpAuthenticationResult authResult =
                    highJumpAuthenticationClient.authenticate(
                            loginUrl,
                            jsonPayload
                    );

            logger.info(
                    "Resposta autenticação coletor {} | HTTP {}",
                    environment.getName(),
                    authResult.responseCode()
            );

            long responseTime =
                    System.currentTimeMillis() - startTime;

            if (authResult.responseCode() == 200) {
                String authenticationTicket =
                        highJumpAuthenticationClient
                                .extractSerializedAuthenticationTicket(
                                        authResult.responseBody()
                                );

                if (authenticationTicket == null || authenticationTicket.isBlank()) {
                    throw new IllegalStateException(
                            "AuthenticationTicket não retornado para o coletor: "
                                    + environment.getName()
                    );
                }

                String registerUrl =
                        highJumpSessionClient.buildRegisterChildSessionUrl(
                                config.getEndpoint()
                        );

                HighJumpSessionClient.HighJumpSessionResult sessionResult =
                        highJumpSessionClient.registerChildSession(
                                registerUrl,
                                authenticationTicket
                        );

                logger.info(
                        "Registro de sessão SCA inMotion do coletor {} | HTTP {}",
                        environment.getName(),
                        sessionResult.responseCode()
                );

                if (sessionResult.responseCode() != 200) {
                    throw new IllegalStateException(
                            "Falha ao registrar sessão SCA inMotion do coletor "
                                    + environment.getName()
                                    + " - HTTP "
                                    + sessionResult.responseCode()
                    );
                }

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
                            + authResult.responseCode()
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
