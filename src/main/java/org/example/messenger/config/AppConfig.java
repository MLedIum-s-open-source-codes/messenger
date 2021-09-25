package org.example.messenger.config;

import org.example.messenger.config.properties.ApplicationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = ApplicationProperties.class)
public class AppConfig {

}
