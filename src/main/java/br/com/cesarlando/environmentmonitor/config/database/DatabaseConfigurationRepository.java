package br.com.cesarlando.environmentmonitor.config.database;


import br.com.cesarlando.environmentmonitor.config.properties.DatabaseEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.config.properties.DatabaseEnvironmentProperties.DatabaseConfigProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class DatabaseConfigurationRepository {

    private final DatabaseEnvironmentProperties databaseEnvironmentProperties;

    public DatabaseConfigurationRepository(DatabaseEnvironmentProperties databaseEnvironmentProperties) {
        this.databaseEnvironmentProperties = databaseEnvironmentProperties;
    }

    public Optional<DatabaseConfigProperties> findByKey(String key) {

        Map<String, DatabaseConfigProperties> databases = databaseEnvironmentProperties.getDatabases();

        return Optional.ofNullable(databases.get(key));
    }
}
