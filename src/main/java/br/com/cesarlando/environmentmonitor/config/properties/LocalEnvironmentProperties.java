package br.com.cesarlando.environmentmonitor.config.properties;

import br.com.cesarlando.environmentmonitor.domain.enums.EnvironmentType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "monitor")
public class LocalEnvironmentProperties {

    private Map<String, EnvironmentConfig> environments =
            new LinkedHashMap<>();

    public Map<String, EnvironmentConfig> getEnvironments() {
        return environments;
    }

    public void setEnvironments(
            Map<String, EnvironmentConfig> environments) {
        this.environments = environments;
    }

    public static class EnvironmentConfig {

        private String name;
        private EnvironmentType type;
        private String endpoint;
        private String username;
        private String password;
        private String tenant;


        public EnvironmentConfig() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public EnvironmentType getType() {
            return type;
        }

        public void setType(EnvironmentType type) {
            this.type = type;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
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

        public String getTenant() {
            return tenant;
        }

        public void setTenant(String tenant) {
            this.tenant = tenant;
        }
    }
}