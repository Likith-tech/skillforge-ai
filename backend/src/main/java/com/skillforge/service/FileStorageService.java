package com.skillforge.service;

import org.springframework.core.io.Resource;

public interface FileStorageService {

    /**
     * Stores the already-read file bytes under a per-user folder and returns the
     * generated (collision-proof) stored file name.
     */
    String store(Long userId, byte[] content, String fileType);

    /** Loads a previously stored file back as a readable {@link Resource}. */
    Resource load(Long userId, String storedFileName);

    /** Removes a previously stored file from disk, if present. */
    void delete(Long userId, String storedFileName);
}
