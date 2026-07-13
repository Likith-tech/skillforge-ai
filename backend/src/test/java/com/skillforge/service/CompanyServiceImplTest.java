package com.skillforge.service;

import com.skillforge.dto.CompanyRequest;
import com.skillforge.dto.CompanyResponse;
import com.skillforge.exception.DuplicateResourceException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.Company;
import com.skillforge.model.User;
import com.skillforge.repository.CompanyRepository;
import com.skillforge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private UserRepository userRepository;

    private CompanyServiceImpl companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyServiceImpl(companyRepository, userRepository);
    }

    private User owner(long id) {
        return User.builder().id(id).fullName("Recruiter " + id).email("r" + id + "@example.com").build();
    }

    private Company company(long id, User owner) {
        return Company.builder().id(id).owner(owner).companyName("Acme Corp").build();
    }

    @Test
    void create_ownerAlreadyHasProfile_throwsDuplicateResourceException() {
        when(companyRepository.existsByOwnerId(10L)).thenReturn(true);
        CompanyRequest request = new CompanyRequest("Acme Corp", null, null, null, null);

        assertThatThrownBy(() -> companyService.create(10L, request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void create_success() {
        when(companyRepository.existsByOwnerId(10L)).thenReturn(false);
        when(userRepository.findById(10L)).thenReturn(Optional.of(owner(10L)));
        CompanyRequest request = new CompanyRequest("Acme Corp", "We build things", "https://acme.example",
                "Remote", null);

        CompanyResponse response = companyService.create(10L, request);

        assertThat(response.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(response.getOwnerId()).isEqualTo(10L);
    }

    @Test
    void update_notTheOwner_throwsAccessDeniedException() {
        Company existing = company(1L, owner(10L));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(existing));
        CompanyRequest request = new CompanyRequest("New Name", null, null, null, null);

        assertThatThrownBy(() -> companyService.update(99L, 1L, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void update_owner_updatesFields() {
        Company existing = company(1L, owner(10L));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(existing));
        CompanyRequest request = new CompanyRequest("New Name", "New desc", null, null, null);

        CompanyResponse response = companyService.update(10L, 1L, request);

        assertThat(response.getCompanyName()).isEqualTo("New Name");
    }

    @Test
    void getById_unknownId_throwsResourceNotFoundException() {
        when(companyRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getById(404L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getMine_noProfileYet_throwsResourceNotFoundException() {
        when(companyRepository.findByOwnerId(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getMine(10L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
