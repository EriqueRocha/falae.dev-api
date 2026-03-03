package dev.falae.application.ports.services;

import java.util.List;

public interface StorageService {
    void deleteFolder(String folderPath);
    void deleteFiles(List<String> filePaths);
}
