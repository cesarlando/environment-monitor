package br.com.cesarlando.environmentmonitor.infrastructure.checker.collector;

import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump.HighJumpAuthenticationClient;
import br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump.HighJumpSessionClient;
import br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump.HighJumpWebTerminalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;

@Component
public class CollectorEnvironmentChecker implements EnvironmentChecker {
    private static final Logger logger = LoggerFactory.getLogger(CollectorEnvironmentChecker.class);
    private final LocalEnvironmentProperties localEnvironmentProperties;
    private final HighJumpAuthenticationClient highJumpAuthenticationClient;
    private final HighJumpSessionClient highJumpSessionClient;
    private final HighJumpWebTerminalClient highJumpWebTerminalClient;

    public CollectorEnvironmentChecker(LocalEnvironmentProperties localEnvironmentProperties, HighJumpAuthenticationClient highJumpAuthenticationClient, HighJumpSessionClient highJumpSessionClient, HighJumpWebTerminalClient highJumpWebTerminalClient) {
        this.localEnvironmentProperties = localEnvironmentProperties;
        this.highJumpAuthenticationClient = highJumpAuthenticationClient;
        this.highJumpSessionClient = highJumpSessionClient;
        this.highJumpWebTerminalClient = highJumpWebTerminalClient;
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

                var claims =
                        highJumpSessionClient.extractIdentityClaims(
                                sessionResult.responseBody()
                        );

                logger.info(
                        "Quantidade de Identity Claims para {}: {}",
                        environment.getName(),
                        claims.size()
                );

                var parameters =
                        highJumpSessionClient.resolveIdentityClaimParameters(
                                config.getEndpoint(),
                                authenticationTicket,
                                claims
                        );

                logger.info(
                        "Identity Claims resolvidas para {}: {}",
                        environment.getName(),
                        parameters.keySet()
                );

                String webServerUrl =
                        parameters.get("JC Web Server Url");

                String appServerName =
                        parameters.get("JC App Server Name");

                String devicePort =
                        parameters.get("JC Device Port");

                String deviceName =
                        parameters.get("JC Device Name");

                String screenSize =
                        parameters.getOrDefault(
                                "JC Screen Size",
                                "8x20");

                if (webServerUrl == null || webServerUrl.isBlank()
                        || appServerName == null || appServerName.isBlank()
                        || devicePort == null || devicePort.isBlank()
                        || deviceName == null || deviceName.isBlank()
                        || screenSize == null || screenSize.isBlank()) {

                    throw new IllegalStateException(
                            "Parâmetros do WebTerminal não encontrados para o coletor: "
                                    + environment.getName()
                    );
                }

                logger.info(
                        "Parâmetros do WebTerminal resolvidos para o coletor {}",
                        environment.getName()
                );

                String webTerminalUrl =
                        highJumpWebTerminalClient.buildInitialUrl(
                                webServerUrl,
                                appServerName,
                                devicePort,
                                deviceName,
                                screenSize
                        );

                HighJumpWebTerminalClient.WebTerminalResult webTerminalResult =
                        highJumpWebTerminalClient.executeInitialRequest(
                                webTerminalUrl,
                                authenticationTicket
                        );

                logger.info(
                        "WebTerminal inicial do coletor {} | HTTP {}",
                        environment.getName(),
                        webTerminalResult.responseCode()
                );

                if (webTerminalResult.responseCode() != 200
                        && webTerminalResult.responseCode() != 301
                        && webTerminalResult.responseCode() != 302) {

                    throw new IllegalStateException(
                            "Falha na validação funcional do WebTerminal do coletor "
                                    + environment.getName()
                                    + " - HTTP "
                                    + webTerminalResult.responseCode()
                    );
                }

                HighJumpWebTerminalClient.WebTerminalResult finalWebTerminalResult =
                        webTerminalResult;

                if (webTerminalResult.responseCode() == 301
                        || webTerminalResult.responseCode() == 302) {

                    String redirectUrl =
                            highJumpWebTerminalClient.extractRedirectUrl(
                                    webServerUrl,
                                    webTerminalResult.responseBody()
                            );

                    finalWebTerminalResult =
                            highJumpWebTerminalClient.executeRedirectRequest(
                                    redirectUrl,
                                    authenticationTicket
                            );

                    logger.info(
                            "WebTerminal após redirect do coletor {} | HTTP {}",
                            environment.getName(),
                            finalWebTerminalResult.responseCode()
                    );
                }

                String finalResponseBody =
                        finalWebTerminalResult.responseBody();

                logger.info(
                        "Resumo WebTerminal final {} | bodyLength={} | engineDown={} | terminalInUse={} | sessionEnd={} | isJsonLike={}",
                        environment.getName(),
                        finalResponseBody != null ? finalResponseBody.length() : 0,
                        finalResponseBody != null
                                && finalResponseBody.toUpperCase().contains("ADVANTAGEWORKFLOWENGINEISDOWN"),
                        finalResponseBody != null
                                && finalResponseBody.toUpperCase().contains("TERMINALISALREADYINUSE"),
                        finalResponseBody != null
                                && finalResponseBody.toUpperCase().contains("ADV_SESSION_END"),
                        finalResponseBody != null
                                && finalResponseBody.trim().startsWith("{")
                );

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

            long responseTime =
                    System.currentTimeMillis() - startTime;

            Throwable cause = exception;

            while (cause != null) {

                if (cause instanceof SocketTimeoutException) {

                    CheckResult checkResult = new CheckResult();

                    checkResult.setEnvironment(environment);
                    checkResult.setStatus(EnvironmentStatus.OFFLINE);
                    checkResult.setCheckedAt(LocalDateTime.now());
                    checkResult.setResponseTime(responseTime);
                    checkResult.setDetails(
                            "Körber One indisponível - timeout no login"
                    );

                    return checkResult;
                }

                cause = cause.getCause();
            }

            throw new IllegalStateException(
                    "Erro ao autenticar no coletor: "
                            + environment.getName(),
                    exception
            );
        }
    }
}
