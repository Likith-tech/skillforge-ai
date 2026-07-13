package com.skillforge.service;

import com.skillforge.dto.AtsHistoryItemResponse;
import com.skillforge.dto.AtsReportResponse;
import com.skillforge.dto.PageResponse;
import com.skillforge.exception.BadRequestException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.AtsAnalysis;
import com.skillforge.model.Resume;
import com.skillforge.model.TargetRole;
import com.skillforge.model.User;
import com.skillforge.repository.AtsAnalysisRepository;
import com.skillforge.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtsReportServiceImplTest {

    @Mock private AtsAnalysisRepository atsAnalysisRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private AtsIntelligenceService atsIntelligenceService;

    private AtsReportServiceImpl atsReportService;

    @BeforeEach
    void setUp() {
        atsReportService = new AtsReportServiceImpl(atsAnalysisRepository, resumeRepository, atsIntelligenceService);
    }

    private Resume resume(long id) {
        return Resume.builder().id(id).user(User.builder().id(1L).build())
                .originalFileName("resume.pdf").build();
    }

    private AtsAnalysis analysis(long id, Resume resume, int score) {
        return AtsAnalysis.builder().id(id).resume(resume).overallScore(score)
                .formattingScore(score).skillsScore(score).educationScore(score).experienceScore(score)
                .projectsScore(score).certificationsScore(score).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void analyze_persistsAndReturnsReport() {
        Resume resume = resume(1L);
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));
        when(atsIntelligenceService.analyze(eq(resume), eq(TargetRole.JAVA_DEVELOPER)))
                .thenReturn(analysis(10L, resume, 88));

        AtsReportResponse response = atsReportService.analyze(1L, "java_developer");

        assertThat(response.getOverallScore()).isEqualTo(88);
        assertThat(response.getStrengthLabel()).isEqualTo("Strong");
        verify(atsAnalysisRepository).save(any(AtsAnalysis.class));
    }

    @Test
    void analyze_unknownResume_throwsResourceNotFoundException() {
        when(resumeRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atsReportService.analyze(404L, null)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void analyze_invalidTargetRole_throwsBadRequestException() {
        Resume resume = resume(1L);
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));

        assertThatThrownBy(() -> atsReportService.analyze(1L, "not-a-real-role"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getLatestReport_existingAnalysis_returnsItWithoutRunningANewOne() {
        Resume resume = resume(1L);
        AtsAnalysis existing = analysis(10L, resume, 55);
        when(atsAnalysisRepository.findFirstByResumeIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(existing));

        AtsReportResponse response = atsReportService.getLatestReport(1L);

        assertThat(response.getOverallScore()).isEqualTo(55);
        assertThat(response.getStrengthLabel()).isEqualTo("Needs Improvement");
        verify(atsIntelligenceService, times(0)).analyze(any(), any());
    }

    @Test
    void getLatestReport_noneExistsYet_generatesAGenericOne() {
        Resume resume = resume(1L);
        when(atsAnalysisRepository.findFirstByResumeIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));
        when(atsIntelligenceService.analyze(eq(resume), org.mockito.ArgumentMatchers.isNull()))
                .thenReturn(analysis(11L, resume, 25));

        AtsReportResponse response = atsReportService.getLatestReport(1L);

        assertThat(response.getStrengthLabel()).isEqualTo("Weak");
        verify(atsAnalysisRepository).save(any(AtsAnalysis.class));
    }

    @Test
    void listHistory_returnsPagedHistory() {
        Resume resume = resume(1L);
        AtsAnalysis a1 = analysis(1L, resume, 60);
        when(atsAnalysisRepository.findByUserId(eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of(a1), PageRequest.of(0, 10), 1));

        PageResponse<AtsHistoryItemResponse> page = atsReportService.listHistory(1L, 0, 10, "highestScore");

        assertThat(page.totalElements()).isEqualTo(1);
        assertThat(page.content().get(0).getOverallScore()).isEqualTo(60);
    }

    @Test
    void listHistory_invalidSortKey_throwsBadRequestException() {
        assertThatThrownBy(() -> atsReportService.listHistory(1L, 0, 10, "bogus"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void deleteHistoryEntry_notOwnedByUser_throwsResourceNotFoundException() {
        when(atsAnalysisRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> atsReportService.deleteHistoryEntry(1L, 5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
