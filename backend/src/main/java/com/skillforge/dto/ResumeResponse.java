package com.skillforge.dto;

import com.skillforge.model.Resume;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumeResponse {

    private Long id;
    private Long studentId;
    private String fileName;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    private String status;
    private boolean previewAvailable;

    public static ResumeResponse from(Resume resume) {
        ResumeResponse response = new ResumeResponse();
        response.setId(resume.getId());
        response.setStudentId(resume.getStudentId());
        response.setFileName(resume.getFileName());
        response.setOriginalFileName(resume.getOriginalFileName());
        response.setFileType(resume.getFileType());
        response.setFileSize(resume.getFileSize());
        response.setUploadedAt(resume.getUploadedAt());
        response.setUpdatedAt(resume.getUpdatedAt());
        response.setStatus(resume.getStatus().name());
        response.setPreviewAvailable("application/pdf".equals(resume.getFileType()));
        return response;
    }
}
