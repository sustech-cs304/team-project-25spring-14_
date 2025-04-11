package com.example.album.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class ApplicationInitializer {
    /**
     * AI-generated-content
     * tool: claude
     * version: latest
     * usage: ask how to use the dir in properties to initialize the storage at the beginning
     * directly copy
     */
    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        // Ensure the main upload directory exists
        createDirectory(uploadDir);

        // Create subdirectories for photos and thumbnails
        createDirectory(uploadDir + "/photos");
        createDirectory(uploadDir + "/thumbnails");
    }

    // Utility method to create a directory
    private void createDirectory(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs(); // Create directories if not exist
            if (created) {
                System.out.println("Directory created: " + dirPath);
            } else {
                System.out.println("Failed to create directory: " + dirPath);
            }
        }
    }
}
