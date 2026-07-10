package com.skillforge.service;

import com.skillforge.dto.FileAssetResponse;
import com.skillforge.model.FileAsset;
import com.skillforge.model.FileAssetType;
import com.skillforge.repository.FileAssetRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileAssetService {

    private final FileAssetRepository fileAssetRepository;
    private final AssetStorageService assetStorageService;

    public FileAssetService(FileAssetRepository fileAssetRepository, AssetStorageService assetStorageService) {
        this.fileAssetRepository = fileAssetRepository;
        this.assetStorageService = assetStorageService;
    }

    @Transactional
    public FileAssetResponse save(Long ownerId, FileAssetType assetType, org.springframework.web.multipart.MultipartFile file) {
        StoredResumeFile storedFile = assetStorageService.store(file, ownerId);
        FileAsset fileAsset = new FileAsset();
        fileAsset.setOwnerId(ownerId);
        fileAsset.setAssetType(assetType);
        fileAsset.setFileName(storedFile.getFileName());
        fileAsset.setOriginalFileName(storedFile.getOriginalFileName());
        fileAsset.setFileType(storedFile.getFileType());
        fileAsset.setFileSize(storedFile.getFileSize());
        fileAsset.setFilePath(storedFile.getFilePath());
        return FileAssetResponse.from(fileAssetRepository.save(fileAsset));
    }

    public List<FileAssetResponse> list(Long ownerId) {
        return fileAssetRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream().map(FileAssetResponse::from).toList();
    }

    @Transactional
    public void delete(Long ownerId, Long fileId) {
        FileAsset fileAsset = fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        if (!fileAsset.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete this file");
        }
        assetStorageService.delete(fileAsset.getFilePath());
        fileAssetRepository.delete(fileAsset);
    }
}