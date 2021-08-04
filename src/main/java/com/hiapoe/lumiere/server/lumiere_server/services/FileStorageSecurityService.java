package com.hiapoe.lumiere.server.lumiere_server.services;

import com.hiapoe.lumiere.server.lumiere_server.properties.FileStorageServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FileStorageSecurityService {

    private final String accessKey;

    @Autowired
    public FileStorageSecurityService(FileStorageServiceProperties fileStorageServiceProperties) {
        this.accessKey = fileStorageServiceProperties.getAccessKey();
    }

    /**
     * Returns true if the given accessKey is equal to the registered accessKey (specified at server startup)
     * @param accessKey the String access key to verify
     * @return true if the given access key is equals to the registered access key
     */
    public boolean isAccessKeyCorrect(String accessKey) {
        return Objects.equals(accessKey, this.accessKey);
    }
}
