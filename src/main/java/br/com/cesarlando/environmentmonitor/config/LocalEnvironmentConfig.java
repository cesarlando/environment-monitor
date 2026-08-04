package br.com.cesarlando.environmentmonitor.config;

import br.com.cesarlando.environmentmonitor.config.properties.LocalEnvironmentProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@EnableConfigurationProperties(LocalEnvironmentProperties.class)
public class LocalEnvironmentConfig {
}