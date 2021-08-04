package com.hiapoe.lumiere.server.lumiere_server.controllers;

import com.hiapoe.lumiere.server.lumiere_server.entities.UploadFileResponse;
import com.hiapoe.lumiere.server.lumiere_server.exceptions.FileStorageException;
import com.hiapoe.lumiere.server.lumiere_server.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class FileStorageController {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageController.class);

    final private FileStorageService fileStorageService;

    @Autowired
    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/uploadFile/{directoryName}")
    public ResponseEntity uploadFile(@PathVariable String directoryName, @RequestParam("file") MultipartFile file,
                                     @RequestParam("access-key") String accessKey) {
        if (Objects.isNull(file)) {
            FileStorageController.logger.warn("/uploadFile/ file cannot be null");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("/uploadFile/ file cannot be null");
        }

        try {
            String fileName = fileStorageService.storeFile(directoryName, file);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/downloadFile/" + directoryName + "/")
                    .path(fileName)
                    .toUriString();

            FileStorageController.logger.info(String.format("File saved /%s/%s", directoryName, fileName));
            return ResponseEntity.ok(new UploadFileResponse(fileName, fileDownloadUri,
                    file.getContentType(), file.getSize()));
        }
        catch (FileStorageException exception) {
            FileStorageController.logger.error(String.format("Unable to upload file /%s/%s", directoryName, file.getOriginalFilename()), exception);
            return ResponseEntity.internalServerError().body(exception);
        }
    }

    @PostMapping("/uploadMultipleFiles/{directoryName}")
    public ResponseEntity uploadMultipleFiles(@PathVariable String directoryName, @RequestParam("files") MultipartFile[] files) {
        if (files.length < 1) {
            FileStorageController.logger.warn("/uploadMultipleFiles/ can't upload an empty list of files");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("/uploadMultipleFiles/ can't upload an empty list of files");
        }

        List<UploadFileResponse> responseList = new ArrayList<>();
        for(MultipartFile file : files) {
            try {
                String filename = this.fileStorageService.storeFile(directoryName, file);
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/downloadFile/" + directoryName + "/")
                        .path(filename)
                        .toUriString();

                responseList.add(new UploadFileResponse(filename, fileDownloadUri,
                        file.getContentType(), file.getSize()));
            }
            catch (FileStorageException exception) {
                FileStorageController.logger.error("Unable to upload file", exception);
            }
        }
        if (responseList.isEmpty()) {
            FileStorageController.logger.error("/uploadMultipleFiles/ was unable to upload any files in the list");
            return ResponseEntity.internalServerError().body("/uploadMultipleFiles/ was unable to upload file any files in the list");
        }
        if (responseList.size() < files.length) {
            FileStorageController.logger.warn("/uploadMultipleFiles/ was unable to upload some files in the list");
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(responseList);
        }
        FileStorageController.logger.info(String.format("List of %d Files saved", files.length));
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/downloadFile/{directoryName}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String directoryName, @PathVariable String fileName,  HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(directoryName, fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        FileStorageController.logger.info(String.format("File /%s/%s downloaded", directoryName, fileName));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
