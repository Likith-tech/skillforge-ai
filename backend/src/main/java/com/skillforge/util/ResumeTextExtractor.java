package com.skillforge.util;

import com.skillforge.exception.UnsupportedFileTypeException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class ResumeTextExtractor {

    public static final String PDF = "PDF";
    public static final String DOCX = "DOCX";

    public String resolveFileType(String originalFileName) {
        String lower = originalFileName == null ? "" : originalFileName.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return PDF;
        }
        if (lower.endsWith(".docx")) {
            return DOCX;
        }
        throw new UnsupportedFileTypeException("Only PDF and DOCX resumes are supported");
    }

    /** Takes the already-read file bytes rather than a MultipartFile, so callers that
     *  also need the bytes for signature/virus-scan validation don't re-read the
     *  upload from disk/network a second time just to extract text. */
    public String extractText(byte[] content, String fileType) {
        try {
            return switch (fileType) {
                case PDF -> extractPdfText(content);
                case DOCX -> extractDocxText(content);
                default -> throw new UnsupportedFileTypeException("Only PDF and DOCX resumes are supported");
            };
        } catch (IOException ex) {
            throw new com.skillforge.exception.FileStorageException("Failed to read resume content", ex);
        }
    }

    private String extractPdfText(byte[] content) throws IOException {
        try (PDDocument document = Loader.loadPDF(content)) {
            return new PDFTextStripper().getText(document);
        }
    }

    private String extractDocxText(byte[] content) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(content));
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }
}
