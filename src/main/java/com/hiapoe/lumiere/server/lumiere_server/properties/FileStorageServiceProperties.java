package com.hiapoe.lumiere.server.lumiere_server.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file-storage-service")
public class FileStorageServiceProperties {
    private String uploadDirectory;
    private String accessKey;
    private String finalDestinationDirectory;
    private String finalDestinationCron;

    public String getUploadDirectory() {
        return uploadDirectory;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getFinalDestinationDirectory() {
        return finalDestinationDirectory;
    }

    public void setFinalDestinationDirectory(String finalDestinationDirectory) {
        this.finalDestinationDirectory = finalDestinationDirectory;
    }

    public String getFinalDestinationCron() {
        return finalDestinationCron;
    }

    public void setFinalDestinationCron(String finalDestinationCron) {
        this.finalDestinationCron = finalDestinationCron;
    }
}
