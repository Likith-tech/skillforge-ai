package com.skillforge.service;

/**
 * Extension point for scanning an uploaded file's bytes before it's persisted.
 * {@link NoOpVirusScanner} is the default bean so the platform runs without an
 * external AV dependency; a production deployment should provide its own
 * {@code @Primary} implementation (e.g. a ClamAV daemon call or a cloud AV API)
 * without touching any caller of this interface.
 */
public interface VirusScanner {

    ScanResult scan(byte[] content, String fileName);

    record ScanResult(boolean clean, String detail) {
        public static ScanResult ok() {
            return new ScanResult(true, null);
        }

        public static ScanResult infected(String detail) {
            return new ScanResult(false, detail);
        }
    }
}
