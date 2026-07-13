package com.skillforge.dto;

import org.springframework.core.io.Resource;

/** Carries a loaded resume file back to the controller for streaming in the HTTP response. */
public record ResumeFileDownload(Resource resource, String fileName, String contentType, Long ownerUserId) {
}
