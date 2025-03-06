package com.billkeeper.billkeeperbackend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "settings")
@Getter
@Setter
public class AppConfig {
    private String serverUrl;
    private String documentsDirectory;
    private String tesseractDataDirectory;
    private String usersDirectory;
}
