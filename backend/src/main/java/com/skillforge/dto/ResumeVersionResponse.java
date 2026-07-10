package com.skillforge.dto;

import com.skillforge.model.ResumeVersion;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeVersionResponse {

    private Long id;
    private Long resumeId;
    private Long studentId;
    private Integer versionNumber;
    private String fileName;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    private String status;
    private boolean previewAvailable;

    public static ResumeVersionResponse from(ResumeVersion version) {
        ResumeVersionResponse response = new ResumeVersionResponse();
        response.setId(version.getId());
        response.setResumeId(version.getResumeId());
        response.setStudentId(version.getStudentId());
        response.setVersionNumber(version.getVersionNumber());
        response.setFileName(version.getFileName());
        response.setOriginalFileName(version.getOriginalFileName());
        response.setFileType(version.getFileType());
        response.setFileSize(version.getFileSize());
        response.setUploadedAt(version.getUploadedAt());
        response.setUpdatedAt(version.getUpdatedAt());
        response.setStatus(version.getStatus().name());
        response.setPreviewAvailable("application/pdf".equals(version.getFileType()));
        return response;
    }
}
