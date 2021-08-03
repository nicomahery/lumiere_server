package com.hiapoe.lumiere.server.lumiere_server.services;

import com.hiapoe.lumiere.server.lumiere_server.exceptions.FileStorageException;
import com.hiapoe.lumiere.server.lumiere_server.properties.FileStorageServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageServiceProperties fileStorageServiceProperties) {
        this.fileStorageLocation = Paths.get(fileStorageServiceProperties.getUploadDirectory()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    public String storeFile(String directoryName, MultipartFile file) throws FileStorageException {
        if (Objects.isNull(file)) {
            throw new FileStorageException("File object cannot be null");
        }
        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }
        if (directoryName.contains("/") || directoryName.contains("\\")) {
            throw new FileStorageException("Sorry! directoryName contains invalid path sequence " + directoryName);
        }

        try {

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(directoryName);
            targetLocation = targetLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }
        catch (IOException exception) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", exception);
        }

    }
}
