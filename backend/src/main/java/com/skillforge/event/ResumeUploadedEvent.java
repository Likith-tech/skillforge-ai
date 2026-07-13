package com.skillforge.event;

/** Published after a resume upload commits, so Module 2's ATS engine can react without Module 1 depending on it. */
public record ResumeUploadedEvent(Long resumeId) {
}
