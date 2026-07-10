package com.skillforge.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResumeStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final Path rootLocation;
    private final long maxFileSizeBytes;

    public ResumeStorageService(
            @Value("${resume.storage.root:uploads/resumes}") String storageRoot,
            @Value("${resume.storage.max-file-size-bytes:5242880}") long maxFileSizeBytes
    ) {
        this.rootLocation = Paths.get(storageRoot).toAbsolutePath().normalize();
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public StoredResumeFile store(MultipartFile file, Long studentId) {
        validate(file);

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = extensionFor(file.getContentType(), originalFileName);
        String storedFileName = UUID.randomUUID() + extension;
        Path studentDirectory = rootLocation.resolve(String.valueOf(studentId)).normalize();
        Path destination = studentDirectory.resolve(storedFileName).normalize();

        if (!destination.startsWith(rootLocation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }

        try {
            Files.createDirectories(studentDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to store resume file");
        }

        return new StoredResumeFile(
                storedFileName,
                originalFileName,
                file.getContentType(),
                file.getSize(),
                destination.toString()
        );
    }

    public Resource loadAsResource(String filePath) {
        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            if (!path.startsWith(rootLocation) || !Files.exists(path)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume file not found");
            }

            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume file not found");
            }
            return resource;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume file not found");
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file is required");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Original file name is required");
        }

        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF and DOCX files are supported");
        }

        String lowerName = originalFileName.toLowerCase(Locale.ROOT);
        if (!lowerName.endsWith(".pdf") && !lowerName.endsWith(".docx")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF and DOCX files are supported");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume file size exceeds the allowed limit");
        }
    }

    private String extensionFor(String contentType, String originalFileName) {
        if ("application/pdf".equals(contentType)) {
            return ".pdf";
        }
        if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            return ".docx";
        }

        int extensionIndex = originalFileName.lastIndexOf('.');
        return extensionIndex >= 0 ? originalFileName.substring(extensionIndex) : "";
    }
}
