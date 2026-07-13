package com.skillforge.service;

import com.skillforge.dto.CompanyRequest;
import com.skillforge.dto.CompanyResponse;
import com.skillforge.exception.DuplicateResourceException;
import com.skillforge.exception.ResourceNotFoundException;
import com.skillforge.model.Company;
import com.skillforge.model.User;
import com.skillforge.repository.CompanyRepository;
import com.skillforge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CompanyResponse create(Long ownerId, CompanyRequest request) {
        if (companyRepository.existsByOwnerId(ownerId)) {
            throw new DuplicateResourceException("You already have a company profile - use update instead");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerId));

        Company company = Company.builder()
                .owner(owner)
                .companyName(request.getCompanyName())
                .description(request.getDescription())
                .website(request.getWebsite())
                .location(request.getLocation())
                .logo(request.getLogo())
                .build();

        companyRepository.save(company);
        return toResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getById(Long id) {
        return toResponse(requireCompany(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getMine(Long ownerId) {
        Company company = companyRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("You haven't created a company profile yet"));
        return toResponse(company);
    }

    @Override
    @Transactional
    public CompanyResponse update(Long ownerId, Long id, CompanyRequest request) {
        Company company = requireCompany(id);
        if (!company.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("You can only update your own company profile");
        }

        company.setCompanyName(request.getCompanyName());
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());
        company.setLogo(request.getLogo());

        companyRepository.save(company);
        return toResponse(company);
    }

    private Company requireCompany(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .ownerId(company.getOwner().getId())
                .companyName(company.getCompanyName())
                .description(company.getDescription())
                .website(company.getWebsite())
                .location(company.getLocation())
                .logo(company.getLogo())
                .createdAt(company.getCreatedAt())
                .build();
    }
}
