package com.skillforge.service;

import com.skillforge.exception.FileStorageException;
import com.skillforge.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Stores resumes on local disk under file.upload-dir/{userId}/{uuid}.{ext}.
 * Swap this out for an S3-backed FileStorageService later without touching
 * any caller - they only depend on the FileStorageService interface.
 */
@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path rootDir;

    public LocalFileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.rootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootDir);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create upload directory: " + rootDir, ex);
        }
    }

    @Override
    public String store(Long userId, byte[] content, String fileType) {
        String extension = fileType.equalsIgnoreCase("PDF") ? ".pdf" : ".docx";
        String storedFileName = UUID.randomUUID() + extension;

        Path target = resolveWithinRoot(userId, storedFileName);

        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store resume file", ex);
        }

        return storedFileName;
    }

    @Override
    public Resource load(Long userId, String storedFileName) {
        Path target = resolveWithinRoot(userId, storedFileName);
        try {
            Resource resource = new UrlResource(target.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Resume file is no longer available on disk");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new FileStorageException("Invalid file path", ex);
        }
    }

    @Override
    public void delete(Long userId, String storedFileName) {
        Path target = resolveWithinRoot(userId, storedFileName);
        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to delete resume file", ex);
        }
    }

    private Path resolveWithinRoot(Long userId, String storedFileName) {
        Path userDir = rootDir.resolve(String.valueOf(userId));
        Path target = userDir.resolve(storedFileName).normalize();

        if (!target.startsWith(rootDir)) {
            throw new FileStorageException("Invalid file path", null);
        }
        return target;
    }
}
