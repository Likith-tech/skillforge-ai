package com.skillforge.dto;

import com.skillforge.model.FileAsset;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAssetResponse {

    private Long id;
    private Long ownerId;
    private String assetType;
    private String fileName;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime createdAt;

    public static FileAssetResponse from(FileAsset fileAsset) {
        FileAssetResponse response = new FileAssetResponse();
        response.setId(fileAsset.getId());
        response.setOwnerId(fileAsset.getOwnerId());
        response.setAssetType(fileAsset.getAssetType().name());
        response.setFileName(fileAsset.getFileName());
        response.setOriginalFileName(fileAsset.getOriginalFileName());
        response.setFileType(fileAsset.getFileType());
        response.setFileSize(fileAsset.getFileSize());
        response.setFilePath(fileAsset.getFilePath());
        response.setCreatedAt(fileAsset.getCreatedAt());
        return response;
    }
}