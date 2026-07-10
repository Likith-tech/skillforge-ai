package com.skillforge.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoredResumeFile {

    private String fileName;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
    private String filePath;
}
