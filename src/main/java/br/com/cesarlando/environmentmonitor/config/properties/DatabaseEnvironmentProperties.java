package br.com.cesarlando.environmentmonitor.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "monitor")
public class DatabaseEnvironmentProperties {
    private Map<String, DatabaseConfigProperties> databases = new LinkedHashMap<>();

    public Map<String, DatabaseConfigProperties> getDatabases() {
        return databases;
    }

    public void setDatabases(Map<String, DatabaseConfigProperties> databases) {
        this.databases = databases;
    }

    public static class DatabaseConfigProperties {
        private String name;
        private String databaseType;
        private String host;
        private Integer port;
        private String serviceName;
        private String database;
        private String username;
        private String password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDatabaseType() {
            return databaseType;
        }

        public void setDatabaseType(String databaseType) {
            this.databaseType = databaseType;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
