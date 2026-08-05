package br.com.cesarlando.environmentmonitor.infrastructure.checker.database;

import br.com.cesarlando.environmentmonitor.config.database.DatabaseConfigurationRepository;
import br.com.cesarlando.environmentmonitor.config.properties.DatabaseEnvironmentProperties.DatabaseConfigProperties;
import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentStatus;
import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;
import br.com.cesarlando.environmentmonitor.domain.ports.EnvironmentChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

@Component
public class DatabaseEnvironmentChecker implements EnvironmentChecker {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEnvironmentChecker.class);
    private static final String DATABASE_PREFIX = "database://";
    private final DatabaseConfigurationRepository databaseConfigurationRepository;

    public DatabaseEnvironmentChecker(DatabaseConfigurationRepository databaseConfigurationRepository) {
        this.databaseConfigurationRepository = databaseConfigurationRepository;
    }

    @Override
    public CheckResult check(Environment environment) {
        logger.info("Executando checker DATABASE para {}", environment.getName());

        logger.info("===== DATABASE CHECKER =====");
        logger.info("Banco: {}", environment.getName());
        logger.info("Endpoint: {}", environment.getEndpoint());

        long startTime = System.currentTimeMillis();

        CheckResult checkResult = new CheckResult();

        checkResult.setEnvironment(environment);
        checkResult.setCheckedAt(LocalDateTime.now());

        try {

            String databaseKey = extractDatabaseKey(environment.getEndpoint());

            DatabaseConfigProperties config =
                    databaseConfigurationRepository
                            .findByKey(databaseKey)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Database configuration not found: "
                                                    + databaseKey
                                    )
                            );

            String jdbcUrl = buildJdbcUrl(config);

            try (
                    Connection connection = DriverManager.getConnection(
                            jdbcUrl,
                            config.getUsername(),
                            config.getPassword()
                    );

                    PreparedStatement statement =
                            connection.prepareStatement(
                                    "SELECT 1 FROM DUAL"
                            );

                    ResultSet resultSet = statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    throw new IllegalStateException(
                            "Database validation query returned no result"
                    );
                }
            }

            long responseTime =
                    System.currentTimeMillis() - startTime;

            checkResult.setStatus(EnvironmentStatus.ONLINE);
            checkResult.setResponseTime(responseTime);
            checkResult.setDetails(
                    "Oracle connection successful"
            );

            logger.info(
                    "Banco {} ONLINE | Tempo: {} ms",
                    environment.getName(),
                    responseTime
            );

        } catch (Exception exception) {

            long responseTime =
                    System.currentTimeMillis() - startTime;

            checkResult.setStatus(EnvironmentStatus.OFFLINE);
            checkResult.setResponseTime(responseTime);

            String errorMessage = limitErrorMessage(
                    exception.getMessage()
            );

            checkResult.setDetails(errorMessage);

            logger.error(
                    "Falha ao verificar banco {} | Tempo: {} ms | Erro: {}",
                    environment.getName(),
                    responseTime,
                    errorMessage
            );
        }

        return checkResult;
    }

    private String extractDatabaseKey(String endpoint) {

        if (endpoint == null ||
                !endpoint.startsWith(DATABASE_PREFIX)) {

            throw new IllegalArgumentException(
                    "Invalid database endpoint: " + endpoint
            );
        }

        return endpoint.substring(DATABASE_PREFIX.length());
    }

    private String buildJdbcUrl(DatabaseConfigProperties config) {

        if (!"ORACLE".equalsIgnoreCase(
                config.getDatabaseType())) {

            throw new IllegalArgumentException(
                    "Unsupported database type: "
                            + config.getDatabaseType()
            );
        }

        return "jdbc:oracle:thin:@//"
                + config.getHost()
                + ":"
                + config.getPort()
                + "/"
                + config.getServiceName();
    }

    private String limitErrorMessage(String errorMessage) {

        if (errorMessage == null) {
            return "Database access error";
        }

        if (errorMessage.length() > 300) {
            return errorMessage.substring(0, 300) + "...";
        }

        return errorMessage;
    }
}

