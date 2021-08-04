package com.hiapoe.lumiere.server.lumiere_server.configuration;

import com.hiapoe.lumiere.server.lumiere_server.interceptors.FileStorageInterceptor;
import com.hiapoe.lumiere.server.lumiere_server.services.FileStorageSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    private final FileStorageSecurityService fileStorageSecurityService;

    @Autowired
    public WebConfigurer(FileStorageSecurityService fileStorageSecurityService) {
        this.fileStorageSecurityService = fileStorageSecurityService;
    }

    @Bean
    public FileStorageInterceptor fileStorageInterceptor() {
        return new FileStorageInterceptor(this.fileStorageSecurityService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(fileStorageInterceptor());
    }
}
