package com.hiapoe.lumiere.server.lumiere_server.exceptions;

public class FileStorageException extends RuntimeException  {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
