package com.skillforge.service;

import com.skillforge.dto.AtsHistoryItemResponse;
import com.skillforge.dto.AtsMissingSkillResponse;
import com.skillforge.dto.AtsReportResponse;
import com.skillforge.dto.AtsSubScores;
import com.skillforge.dto.PageResponse;
import com.skillforge.exception.BadRequestException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.AtsAnalysis;
import com.skillforge.model.MissingSkillEntry;
import com.skillforge.model.Resume;
import com.skillforge.model.TargetRole;
import com.skillforge.repository.AtsAnalysisRepository;
import com.skillforge.repository.ResumeRepository;
import com.skillforge.util.PageRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AtsReportServiceImpl implements AtsReportService {

    private final AtsAnalysisRepository atsAnalysisRepository;
    private final ResumeRepository resumeRepository;
    private final AtsIntelligenceService atsIntelligenceService;

    @Override
    @Transactional
    public AtsReportResponse analyze(Long resumeId, String targetRoleRaw) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found: " + resumeId));

        TargetRole targetRole = parseTargetRole(targetRoleRaw);
        AtsAnalysis analysis = atsIntelligenceService.analyze(resume, targetRole);
        atsAnalysisRepository.save(analysis);
        return toResponse(analysis);
    }

    @Override
    @Transactional
    public AtsReportResponse getLatestReport(Long resumeId) {
        return atsAnalysisRepository.findFirstByResumeIdOrderByCreatedAtDesc(resumeId)
                .map(this::toResponse)
                .orElseGet(() -> analyze(resumeId, null));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AtsHistoryItemResponse> listHistory(Long userId, Integer page, Integer size, String sort) {
        Pageable pageable = PageRequestFactory.of(page, size, resolveSort(sort));
        Page<AtsAnalysis> history = atsAnalysisRepository.findByUserId(userId, pageable);
        return PageResponse.from(history.map(this::toHistoryItem));
    }

    private Sort resolveSort(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortKey) {
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "highestScore" -> Sort.by(Sort.Direction.DESC, "overallScore");
            default -> throw new BadRequestException(
                    "Invalid sort key: " + sortKey + ". Valid values: newest, oldest, highestScore");
        };
    }

    @Override
    @Transactional
    public void deleteHistoryEntry(Long userId, Long analysisId) {
        AtsAnalysis analysis = atsAnalysisRepository.findByIdAndUserId(analysisId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found: " + analysisId));
        atsAnalysisRepository.delete(analysis);
    }

    private TargetRole parseTargetRole(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        try {
            return TargetRole.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid target role: " + raw + ". Valid values: " + Arrays.toString(TargetRole.values()));
        }
    }

    private String strengthLabel(int score) {
        if (score >= 80) return "Strong";
        if (score >= 60) return "Good";
        if (score >= 40) return "Needs Improvement";
        return "Weak";
    }

    private AtsReportResponse toResponse(AtsAnalysis analysis) {
        return AtsReportResponse.builder()
                .id(analysis.getId())
                .resumeId(analysis.getResume().getId())
                .targetRole(analysis.getTargetRole() != null ? analysis.getTargetRole().name() : null)
                .overallScore(analysis.getOverallScore())
                .strengthLabel(strengthLabel(analysis.getOverallScore()))
                .subScores(AtsSubScores.builder()
                        .formatting(analysis.getFormattingScore())
                        .skills(analysis.getSkillsScore())
                        .education(analysis.getEducationScore())
                        .experience(analysis.getExperienceScore())
                        .projects(analysis.getProjectsScore())
                        .certifications(analysis.getCertificationsScore())
                        .build())
                .extractedName(analysis.getExtractedName())
                .extractedEmail(analysis.getExtractedEmail())
                .extractedPhone(analysis.getExtractedPhone())
                // Explicitly copied (not just reference-passed) so every @ElementCollection is
                // forced to initialize here, inside the transaction - passing the lazy proxy
                // straight through let Jackson try to initialize it later during response
                // serialization, outside the session, which threw LazyInitializationException.
                .skillBreakdown(new HashMap<>(analysis.getSkillBreakdown()))
                .missingSkills(analysis.getMissingSkills().stream()
                        .map(this::toMissingSkillResponse)
                        .collect(Collectors.toList()))
                .suggestions(new ArrayList<>(analysis.getSuggestions()))
                .analyzedAt(analysis.getCreatedAt())
                .build();
    }

    private AtsMissingSkillResponse toMissingSkillResponse(MissingSkillEntry entry) {
        return AtsMissingSkillResponse.builder()
                .skillName(entry.getSkillName())
                .priority(entry.getPriority().name())
                .build();
    }

    private AtsHistoryItemResponse toHistoryItem(AtsAnalysis analysis) {
        return AtsHistoryItemResponse.builder()
                .id(analysis.getId())
                .resumeId(analysis.getResume().getId())
                .resumeFileName(analysis.getResume().getOriginalFileName())
                .targetRole(analysis.getTargetRole() != null ? analysis.getTargetRole().name() : null)
                .overallScore(analysis.getOverallScore())
                .strengthLabel(strengthLabel(analysis.getOverallScore()))
                .analyzedAt(analysis.getCreatedAt())
                .build();
    }
}
