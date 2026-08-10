package br.com.cesarlando.environmentmonitor.config;

import br.com.cesarlando.environmentmonitor.config.properties.DatabaseEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import br.com.cesarlando.environmentmonitor.config.properties.TeamsNotificationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@EnableConfigurationProperties({
        LocalEnvironmentProperties.class,
        DatabaseEnvironmentProperties.class,
        TeamsNotificationProperties.class
})
public class LocalEnvironmentConfig {
}