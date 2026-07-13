package com.skillforge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Default {@link VirusScanner}: always reports clean. This exists purely as the
 * wiring point production deployments are expected to replace with a real
 * scanning backend - callers (ResumeServiceImpl) never change either way.
 */
@Slf4j
@Service
public class NoOpVirusScanner implements VirusScanner {

    @Override
    public ScanResult scan(byte[] content, String fileName) {
        log.debug("Virus scan skipped for '{}' - no VirusScanner backend is configured", fileName);
        return ScanResult.ok();
    }
}
