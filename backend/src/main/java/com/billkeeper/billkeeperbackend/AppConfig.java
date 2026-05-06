package com.billkeeper.billkeeperbackend;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;

@Configuration
@ConfigurationProperties(prefix = "settings")
@Getter
@Setter
public class AppConfig {
    private String serverUrl;
    private String frontUrl;
    private String documentsDirectory;
    private String tesseractDataDirectory;
    private String tesseractLanguage;
    private String usersDirectory;
    private Boolean ocrEnabled;

    @PostConstruct
    public void init() {
        Arrays.asList(documentsDirectory, tesseractDataDirectory, usersDirectory).forEach(directory -> {
            if (directory != null && !directory.isEmpty()) {
                createFolderIfNoPresent(directory);
            }
        });
    }

    private void createFolderIfNoPresent(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

}
