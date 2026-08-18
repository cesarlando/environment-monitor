package br.com.cesarlando.environmentmonitor.infrastructure.checker.web;

import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import br.com.cesarlando.environmentmonitor.infrastructure.authentication.allocation.AllocationAuthenticationClient;
import br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump.HighJumpAuthenticationClient;
import br.com.cesarlando.environmentmonitor.infrastructure.checker.http.HttpEnvironmentChecker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WebEnvironmentChecker implements EnvironmentChecker {

    private final LocalEnvironmentProperties localEnvironmentProperties;
    private final HighJumpAuthenticationClient highJumpAuthenticationClient;
    private final AllocationAuthenticationClient allocationAuthenticationClient;

    private final HttpEnvironmentChecker httpEnvironmentChecker;

    public WebEnvironmentChecker(LocalEnvironmentProperties localEnvironmentProperties, HighJumpAuthenticationClient highJumpAuthenticationClient, AllocationAuthenticationClient allocationAuthenticationClient, HttpEnvironmentChecker httpEnvironmentChecker) {
        this.localEnvironmentProperties = localEnvironmentProperties;
        this.highJumpAuthenticationClient = highJumpAuthenticationClient;
        this.allocationAuthenticationClient = allocationAuthenticationClient;
        this.httpEnvironmentChecker = httpEnvironmentChecker;
    }

    @Override
    public CheckResult check(Environment environment) {

        LocalEnvironmentProperties.EnvironmentConfig config = findEnvironmentConfig(environment);

        String authenticationType = config.getAuthenticationType();

        if (authenticationType == null || authenticationType.isBlank()) {
            throw new IllegalStateException(
                    "Tipo de autenticação WEB não configurado: "
                            + environment.getName()
            );
        }
        return switch (authenticationType) {

            case "HIGHJUMP" -> checkHighJump(environment, config);

            case "ALLOCATION" -> checkAllocation(environment, config);

            case "HTTP" -> httpEnvironmentChecker.check(environment);

            default -> throw new IllegalStateException(
                    "Tipo de autenticação WEB não suportado: " + authenticationType
            );
        };
    }
    private CheckResult checkHighJump(Environment environment, LocalEnvironmentProperties.EnvironmentConfig config) {
        long startTime = System.currentTimeMillis();

        if (config.getUsername() == null || config.getUsername().isBlank()) {
            throw new IllegalStateException(
                    "Usuário WEB HighJump não configurado: "
                            + environment.getName()
            );
        }

        if (config.getPassword() == null || config.getPassword().isBlank()) {
            throw new IllegalStateException(
                    "Senha WEB HighJump não configurada: "
                            + environment.getName()
            );
        }

        String loginUrl =
                highJumpAuthenticationClient.buildWebLoginUrl(
                        config.getEndpoint(),
                        config.getAuthenticationPath()
                );

        String jsonPayload =
                highJumpAuthenticationClient.buildLoginPayload(config);

        HighJumpAuthenticationClient.HighJumpAuthenticationResult authResult =
                highJumpAuthenticationClient.authenticate(
                        loginUrl,
                        jsonPayload
                );

        long responseTime =
                System.currentTimeMillis() - startTime;

        if (authResult.responseCode() == 200) {

            CheckResult checkResult = new CheckResult();

            checkResult.setEnvironment(environment);
            checkResult.setStatus(EnvironmentStatus.ONLINE);
            checkResult.setCheckedAt(LocalDateTime.now());
            checkResult.setResponseTime(responseTime);
            checkResult.setDetails(
                    "Login WEB HighJump realizado com sucesso - HTTP 200"
            );

            return checkResult;
        }

        throw new IllegalStateException(
                "Falha na autenticação WEB HighJump "
                        + environment.getName()
                        + " - HTTP "
                        + authResult.responseCode()
        );
    }


    private CheckResult checkAllocation(Environment environment, LocalEnvironmentProperties.EnvironmentConfig config) {

        long startTime = System.currentTimeMillis();

        if (config.getUsername() == null || config.getUsername().isBlank()) {
            throw new IllegalStateException(
                    "Usuário Allocation não configurado: "
                            + environment.getName()
            );
        }

        if (config.getPassword() == null || config.getPassword().isBlank()) {
            throw new IllegalStateException(
                    "Senha Allocation não configurada: "
                            + environment.getName()
            );
        }

        AllocationAuthenticationClient.AllocationLoginPageData loginPageData =
                allocationAuthenticationClient.loadLoginPage(
                        config.getEndpoint()
                );

        AllocationAuthenticationClient.AllocationAuthenticationResult authResult =
                allocationAuthenticationClient.authenticate(
                        config.getEndpoint(),
                        config.getUsername(),
                        config.getPassword(),
                        loginPageData
                );

        long responseTime =
                System.currentTimeMillis() - startTime;

        if (authResult.responseCode() == 200
                && authResult.responseBody().contains("<redirect>")
                && authResult.responseBody().contains("/Home")) {

            CheckResult checkResult = new CheckResult();

            checkResult.setEnvironment(environment);
            checkResult.setStatus(EnvironmentStatus.ONLINE);
            checkResult.setCheckedAt(LocalDateTime.now());
            checkResult.setResponseTime(responseTime);
            checkResult.setDetails(
                    "Login Allocation realizado com sucesso - HTTP 200"
            );

            return checkResult;
        }

        if (authResult.responseBody().contains(
                "Incorrect username or password")) {

            CheckResult checkResult = new CheckResult();

            checkResult.setEnvironment(environment);
            checkResult.setStatus(EnvironmentStatus.OFFLINE);
            checkResult.setCheckedAt(LocalDateTime.now());
            checkResult.setResponseTime(responseTime);
            checkResult.setDetails(
                    "Falha de autenticação: usuário ou senha inválidos"
            );

            return checkResult;
        }

        CheckResult checkResult = new CheckResult();

        checkResult.setEnvironment(environment);
        checkResult.setStatus(EnvironmentStatus.OFFLINE);
        checkResult.setCheckedAt(LocalDateTime.now());
        checkResult.setResponseTime(responseTime);
        checkResult.setDetails(
                "Allocation respondeu HTTP "
                        + authResult.responseCode()
                        + ", mas a autenticação não foi confirmada"
        );

        return checkResult;
    }
    private LocalEnvironmentProperties.EnvironmentConfig
    findEnvironmentConfig(Environment environment) {

        return localEnvironmentProperties
                .getEnvironments()
                .values()
                .stream()
                .filter( config -> config.getName().equals(environment.getName())
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Configuração não encontrada para o ambiente: " + environment.getName()
                        ));
    }
}
