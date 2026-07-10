package com.skillforge.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class ResumeFileResource {

    private Resource resource;
    private String originalFileName;
    private String fileType;
    private Long fileSize;
}
