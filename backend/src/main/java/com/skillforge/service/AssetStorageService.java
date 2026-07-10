package com.skillforge.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssetStorageService {

    private final Path rootLocation;

    public AssetStorageService(@Value("${asset.storage.root:uploads/assets}") String storageRoot) {
        this.rootLocation = Paths.get(storageRoot).toAbsolutePath().normalize();
    }

    public StoredResumeFile store(MultipartFile file, Long ownerId) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = extensionFor(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;
        Path ownerDirectory = rootLocation.resolve(String.valueOf(ownerId)).normalize();
        Path destination = ownerDirectory.resolve(storedFileName).normalize();

        if (!destination.startsWith(rootLocation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }

        try {
            Files.createDirectories(ownerDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to store file");
        }

        return new StoredResumeFile(storedFileName, originalFileName, file.getContentType(), file.getSize(), destination.toString());
    }

    public void delete(String filePath) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            if (path.startsWith(rootLocation)) {
                Files.deleteIfExists(path);
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete file");
        }
    }

    private String extensionFor(String originalFileName) {
        int extensionIndex = originalFileName.lastIndexOf('.');
        return extensionIndex >= 0 ? originalFileName.substring(extensionIndex) : "";
    }
}