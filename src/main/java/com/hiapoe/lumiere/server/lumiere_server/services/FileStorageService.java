package com.hiapoe.lumiere.server.lumiere_server.services;

import com.hiapoe.lumiere.server.lumiere_server.exceptions.FileNotFoundException;
import com.hiapoe.lumiere.server.lumiere_server.exceptions.FileStorageException;
import com.hiapoe.lumiere.server.lumiere_server.properties.FileStorageServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
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
        this.createDirectory(this.fileStorageLocation);
    }

    /**
     * Stores the given file to the directory names directoryName
     * @param directoryName name of the directory in which the file will be saved
     * @param file {@code MultipartFile} to be saved to the given directory
     * @return the name of the saved file
     * @throws FileStorageException if the directory name or the filename is incorrect, or if there is a problem during the file saving
     */
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

        // Copy file to the target location (Replacing existing file with the same name)
        Path targetLocation = this.fileStorageLocation.resolve(directoryName);
        this.createDirectory(targetLocation);
        targetLocation = targetLocation.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }
        catch (IOException exception) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", exception);
        }

    }

    /**
     * Finds the associated file with name fileName and directoryName and returns it has a Resource
     * @param directoryName the name as String of the directory
     * @param fileName the name as String of the file to find
     * @return the found file as a Resource with name fileName and inside the directory named directoryName
     * @throws FileNotFoundException if the file is not found due to read error or bad fileName/directoryName
     */
    public Resource loadFileAsResource(String directoryName, String fileName) throws FileNotFoundException {
        try {
            Path filePath = this.fileStorageLocation.resolve(directoryName).normalize();
            filePath = filePath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("File not found " + fileName, e);
        }
    }

    /**
     * Creates a directory located at the given path
     * @param path the Path location of the new directory
     * @throws FileStorageException if the directory making raised an Exception
     */
    private void createDirectory(Path path) throws FileStorageException  {
        try {
            Files.createDirectories(path);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
}
