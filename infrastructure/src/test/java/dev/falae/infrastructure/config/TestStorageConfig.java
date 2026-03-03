package dev.falae.infrastructure.config;

import dev.falae.application.ports.services.StorageService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

@TestConfiguration
public class TestStorageConfig {

    @Bean
    @Primary
    public StorageService storageService() {
        return new StorageService() {
            @Override
            public void deleteFolder(String folderPath) {
                //No-op for tests
            }

            @Override
            public void deleteFiles(List<String> filePaths) {
                //No-op for tests
            }
        };
    }
}
