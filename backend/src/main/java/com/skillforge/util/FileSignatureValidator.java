package com.skillforge.util;

import com.skillforge.exception.UnsupportedFileTypeException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Verifies a resume upload actually is what its extension claims, using two
 * independent signals on top of {@link ResumeTextExtractor}'s extension check:
 * the client-declared MIME type, and the file's own magic-byte signature. A
 * renamed .exe or .html file with a .pdf extension fails here before it's ever
 * parsed or stored.
 */
@Component
public class FileSignatureValidator {

    private static final byte[] PDF_MAGIC = {0x25, 0x50, 0x44, 0x46}; // "%PDF"
    private static final byte[] ZIP_MAGIC = {0x50, 0x4B, 0x03, 0x04}; // "PK\3\4" - DOCX is a zip container

    private static final Map<String, String> EXPECTED_CONTENT_TYPE = Map.of(
            ResumeTextExtractor.PDF, "application/pdf",
            ResumeTextExtractor.DOCX, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final Map<String, byte[]> EXPECTED_MAGIC = Map.of(
            ResumeTextExtractor.PDF, PDF_MAGIC,
            ResumeTextExtractor.DOCX, ZIP_MAGIC
    );

    /** Rejects a declared Content-Type that's inconsistent with the resolved extension. */
    public void validateDeclaredContentType(String fileType, String declaredContentType) {
        String expected = EXPECTED_CONTENT_TYPE.get(fileType);
        if (expected != null && declaredContentType != null && !expected.equalsIgnoreCase(declaredContentType)) {
            throw new UnsupportedFileTypeException(
                    "Declared content type '" + declaredContentType + "' does not match a " + fileType + " file");
        }
    }

    /** Rejects file content whose first bytes don't match the expected signature for fileType. */
    public void validateMagicBytes(byte[] content, String fileType) {
        byte[] magic = EXPECTED_MAGIC.get(fileType);
        if (magic == null) {
            throw new UnsupportedFileTypeException("Only PDF and DOCX resumes are supported");
        }
        if (content == null || content.length < magic.length || !startsWith(content, magic)) {
            throw new UnsupportedFileTypeException(
                    "File content does not match a valid " + fileType + " file (signature check failed)");
        }
    }

    private boolean startsWith(byte[] content, byte[] magic) {
        for (int i = 0; i < magic.length; i++) {
            if (content[i] != magic[i]) {
                return false;
            }
        }
        return true;
    }
}
