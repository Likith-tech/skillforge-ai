package com.skillforge.service;

import com.skillforge.event.ResumeUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Auto-triggers a generic Module 2 ATS report whenever a resume finishes
 * uploading, satisfying "when a student uploads a resume, the system should
 * automatically parse/score/etc." AFTER_COMMIT means this only runs once the
 * upload has definitely succeeded, and a failure here can't undo the upload -
 * it's caught and logged instead of propagated.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AtsAnalysisEventListener {

    private final AtsReportService atsReportService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onResumeUploaded(ResumeUploadedEvent event) {
        try {
            atsReportService.analyze(event.resumeId(), null);
        } catch (Exception ex) {
            log.error("Auto ATS analysis failed for resume {}", event.resumeId(), ex);
        }
    }
}
