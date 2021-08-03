package com.hiapoe.lumiere.server.lumiere_server;

import com.hiapoe.lumiere.server.lumiere_server.properties.FileStorageServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageServiceProperties.class
})
public class LumiereServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LumiereServerApplication.class, args);
    }

}
