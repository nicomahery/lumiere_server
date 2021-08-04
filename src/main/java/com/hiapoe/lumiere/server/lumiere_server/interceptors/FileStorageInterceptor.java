package com.hiapoe.lumiere.server.lumiere_server.interceptors;

import com.hiapoe.lumiere.server.lumiere_server.services.FileStorageSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileStorageInterceptor implements HandlerInterceptor {

    private final static Logger logger = LoggerFactory.getLogger(FileStorageInterceptor.class);
    private final FileStorageSecurityService fileStorageSecurityService;

    public FileStorageInterceptor(FileStorageSecurityService fileStorageSecurityService) {
        this.fileStorageSecurityService = fileStorageSecurityService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessKey = request.getParameter("access-key");
        boolean isAccessKeyCorrect = this.fileStorageSecurityService.isAccessKeyCorrect(accessKey);
        if (!isAccessKeyCorrect) {
            FileStorageInterceptor.logger.warn(String.format("Invalid access-key: %s", accessKey));
        }
        return isAccessKeyCorrect;
    }
}
