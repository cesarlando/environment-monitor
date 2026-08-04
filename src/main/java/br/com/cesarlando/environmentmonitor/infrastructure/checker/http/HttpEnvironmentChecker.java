package br.com.cesarlando.environmentmonitor.infrastructure.checker.http;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;

import java.net.http.HttpTimeoutException;

import java.time.LocalDateTime;

@Component
public class HttpEnvironmentChecker implements EnvironmentChecker {
    private static final Logger logger = LoggerFactory.getLogger(HttpEnvironmentChecker.class);

    private final RestClient restClient;

    public HttpEnvironmentChecker(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public CheckResult check(Environment environment) {
        logger.info("Executando checker HTTP para {}", environment.getName());

        long startTime = System.currentTimeMillis();

        CheckResult checkResult = new CheckResult();
        checkResult.setEnvironment(environment);
        checkResult.setCheckedAt(LocalDateTime.now());
        try {
            var response = restClient
                    .get()
                    .uri(environment.getEndpoint())
                    .retrieve()
                    .toBodilessEntity();

            long responseTime = System.currentTimeMillis() - startTime;

            checkResult.setStatus(EnvironmentStatus.ONLINE);
            checkResult.setResponseTime(responseTime);
            checkResult.setDetails(response.getStatusCode().toString());

            logger.info("Status HTTP: {} | Tempo de resposta: {} ms", response.getStatusCode(), responseTime);

        } catch (ResourceAccessException exception) {
            long responseTime = System.currentTimeMillis() - startTime;

            checkResult.setStatus(EnvironmentStatus.OFFLINE);
            checkResult.setResponseTime(responseTime);

            Throwable cause = exception.getCause();

            String errorMessage = exception.getMessage();

            if (cause instanceof HttpTimeoutException) {
                errorMessage = "Timeout after 5 seconds";
            }
            if (errorMessage != null && errorMessage.length() > 300) {
                errorMessage = errorMessage.substring(0, 300) + "...";
            }
            checkResult.setDetails(errorMessage);

            logger.error("Falha ao verificar {} | Tempo: {} ms | Erro: {}", environment.getName(), responseTime, errorMessage);


        } catch (Exception exception) {

            long responseTime = System.currentTimeMillis() - startTime;

            checkResult.setStatus(EnvironmentStatus.OFFLINE);
            checkResult.setResponseTime(responseTime);

            String errorMessage = exception.getMessage();

            if (errorMessage != null && errorMessage.length() > 300) {
                errorMessage = errorMessage.substring(0, 300) + "...";
            }

            checkResult.setDetails(
                    errorMessage != null
                            ? errorMessage
                            : exception.getClass().getSimpleName()
            );

            logger.error(
                    "Falha inesperada ao verificar {} | Tempo: {} ms | Erro: {}",
                    environment.getName(),
                    responseTime,
                    errorMessage
            );
        }
        return checkResult;
    }
}