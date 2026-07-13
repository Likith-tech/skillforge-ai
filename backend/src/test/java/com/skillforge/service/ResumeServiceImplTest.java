package com.skillforge.service;

import com.skillforge.dto.AtsAnalysisResult;
import com.skillforge.dto.PageResponse;
import com.skillforge.dto.ResumeSummaryResponse;
import com.skillforge.exception.BadRequestException;
import com.skillforge.exception.MaliciousFileException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.Resume;
import com.skillforge.model.User;
import com.skillforge.repository.ApplicationRepository;
import com.skillforge.repository.ResumeRepository;
import com.skillforge.repository.SkillRepository;
import com.skillforge.repository.UserRepository;
import com.skillforge.util.FileSignatureValidator;
import com.skillforge.util.ResumeTextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceImplTest {

    @Mock private ResumeRepository resumeRepository;
    @Mock private UserRepository userRepository;
    @Mock private SkillRepository skillRepository;
    @Mock private ResumeTextExtractor resumeTextExtractor;
    @Mock private FileStorageService fileStorageService;
    @Mock private SkillExtractionService skillExtractionService;
    @Mock private AtsScoringService atsScoringService;
    @Mock private ApplicationRepository applicationRepository;
    @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;
    @Mock private FileSignatureValidator fileSignatureValidator;
    @Mock private VirusScanner virusScanner;

    private ResumeServiceImpl resumeService;

    @BeforeEach
    void setUp() {
        resumeService = new ResumeServiceImpl(resumeRepository, userRepository, skillRepository,
                resumeTextExtractor, fileStorageService, skillExtractionService, atsScoringService,
                applicationRepository, eventPublisher, fileSignatureValidator, virusScanner);
    }

    private User user(long id) {
        return User.builder().id(id).fullName("Test User").email("u" + id + "@example.com").build();
    }

    private Resume resume(long id, User owner) {
        return Resume.builder().id(id).user(owner).originalFileName("resume.pdf").fileType("PDF")
                .storedFileName("uuid.pdf").fileSizeBytes(1024).skills(Set.of())
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    void upload_nullOrEmptyFile_throwsBadRequestException() {
        assertThatThrownBy(() -> resumeService.upload(1L, null)).isInstanceOf(BadRequestException.class);

        MockMultipartFile empty = new MockMultipartFile("file", "resume.pdf", "application/pdf", new byte[0]);
        assertThatThrownBy(() -> resumeService.upload(1L, empty)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void upload_maliciousFile_throwsMaliciousFileExceptionAndNeverSaves() {
        User owner = user(1L);
        byte[] content = "%PDF-fake".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", content);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(resumeTextExtractor.resolveFileType("resume.pdf")).thenReturn("PDF");
        when(virusScanner.scan(any(), anyString())).thenReturn(VirusScanner.ScanResult.infected("EICAR-TEST"));

        assertThatThrownBy(() -> resumeService.upload(1L, file)).isInstanceOf(MaliciousFileException.class);

        verify(resumeRepository, never()).save(any());
        verify(fileStorageService, never()).store(any(), any(), any());
    }

    @Test
    void upload_success_savesResumeAndPublishesEvent() {
        User owner = user(1L);
        byte[] content = "%PDF-fake".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", content);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(resumeTextExtractor.resolveFileType("resume.pdf")).thenReturn("PDF");
        when(virusScanner.scan(any(), anyString())).thenReturn(VirusScanner.ScanResult.ok());
        when(resumeTextExtractor.extractText(any(), eq("PDF"))).thenReturn("Java Spring Boot developer");
        when(fileStorageService.store(eq(1L), any(), eq("PDF"))).thenReturn("generated-uuid.pdf");
        when(skillRepository.findAll()).thenReturn(List.of());
        when(skillExtractionService.extractSkills(anyString(), any())).thenReturn(Set.of());
        when(atsScoringService.analyze(anyString(), any())).thenReturn(
                AtsAnalysisResult.builder().score(72).missingCoreSkills(Set.of()).suggestions(List.of()).build());

        var response = resumeService.upload(1L, file);

        assertThat(response.getAtsScore()).isEqualTo(72);
        verify(resumeRepository, times(1)).save(any(Resume.class));
        // Explicit type, not bare any(): ApplicationEventPublisher has both
        // publishEvent(ApplicationEvent) and publishEvent(Object) overloads, and an
        // untyped any() can bind to the other one than the code path actually calls.
        verify(eventPublisher, times(1)).publishEvent(any(com.skillforge.event.ResumeUploadedEvent.class));
    }

    @Test
    void upload_userNotFound_throwsResourceNotFoundException() {
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", "x".getBytes());
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.upload(99L, file)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteResume_notOwnedByUser_throwsResourceNotFoundException() {
        when(resumeRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.deleteResume(1L, 5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteResume_alreadyDeleted_throwsResourceNotFoundException() {
        Resume resume = resume(5L, user(1L));
        resume.setDeleted(true);
        when(resumeRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(resume));

        assertThatThrownBy(() -> resumeService.deleteResume(1L, 5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteResume_keepsFileOnDiskWhenAnApplicationStillReferencesIt() {
        Resume resume = resume(5L, user(1L));
        when(resumeRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByResumeId(5L)).thenReturn(true);

        resumeService.deleteResume(1L, 5L);

        assertThat(resume.isDeleted()).isTrue();
        verify(fileStorageService, never()).delete(any(), any());
    }

    @Test
    void deleteResume_reclaimsDiskSpaceWhenNoApplicationReferencesIt() {
        Resume resume = resume(5L, user(1L));
        when(resumeRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByResumeId(5L)).thenReturn(false);

        resumeService.deleteResume(1L, 5L);

        verify(fileStorageService).delete(1L, "uuid.pdf");
    }

    @Test
    void assertOwner_owner_doesNotThrow() {
        Resume resume = resume(5L, user(1L));
        when(resumeRepository.findById(5L)).thenReturn(Optional.of(resume));

        resumeService.assertOwner(5L, 1L);
    }

    @Test
    void assertOwner_notOwner_throwsAccessDeniedException() {
        Resume resume = resume(5L, user(1L));
        when(resumeRepository.findById(5L)).thenReturn(Optional.of(resume));

        assertThatThrownBy(() -> resumeService.assertOwner(5L, 2L)).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void assertViewable_admin_bypassesOwnershipCheckEntirely() {
        resumeService.assertViewable(5L, 999L, true);
        // No repository lookup at all for an admin caller.
        verify(resumeRepository, never()).findById(any());
    }

    @Test
    void assertViewable_ownerStudent_allowed() {
        Resume resume = resume(5L, user(1L));
        when(resumeRepository.findById(5L)).thenReturn(Optional.of(resume));

        resumeService.assertViewable(5L, 1L, false);
    }

    @Test
    void assertViewable_recruiterWithApplicationOnThisResume_allowed() {
        Resume resume = resume(5L, user(1L));
        when(resumeRepository.findById(5L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByResume_IdAndJob_PostedById(5L, 42L)).thenReturn(true);

        resumeService.assertViewable(5L, 42L, false);
    }

    @Test
    void assertViewable_recruiterWithNoApplicationOnThisResume_throwsAccessDeniedException() {
        Resume resume = resume(5L, user(1L));
        when(resumeRepository.findById(5L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByResume_IdAndJob_PostedById(5L, 42L)).thenReturn(false);

        assertThatThrownBy(() -> resumeService.assertViewable(5L, 42L, false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void listHistory_marksTheMostRecentUploadAsCurrent_regardlessOfRequestedSort() {
        User owner = user(1L);
        Resume older = resume(1L, owner);
        Resume newest = resume(2L, owner);

        // "newest" (id=2) is the true current resume even though this page/sort
        // request puts "older" (id=1) first - this is the phase-2 correctness fix.
        when(resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L))
                .thenReturn(Optional.of(newest));
        when(resumeRepository.findByUserIdAndDeletedFalse(eq(1L), any()))
                .thenReturn(new PageImpl<>(List.of(older, newest), PageRequest.of(0, 10), 2));

        PageResponse<ResumeSummaryResponse> page = resumeService.listHistory(1L, 0, 10, "oldest");

        ResumeSummaryResponse olderDto = page.content().stream().filter(r -> r.getId() == 1L).findFirst().orElseThrow();
        ResumeSummaryResponse newestDto = page.content().stream().filter(r -> r.getId() == 2L).findFirst().orElseThrow();
        assertThat(olderDto.isCurrent()).isFalse();
        assertThat(newestDto.isCurrent()).isTrue();
    }

    @Test
    void listHistory_invalidSortKey_throwsBadRequestException() {
        when(resumeRepository.findFirstByUserIdAndDeletedFalseOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.listHistory(1L, 0, 10, "bogus"))
                .isInstanceOf(BadRequestException.class);
    }
}
