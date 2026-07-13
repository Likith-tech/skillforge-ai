package com.skillforge.controller;

import com.skillforge.dto.CompanyRequest;
import com.skillforge.dto.CompanyResponse;
import com.skillforge.security.SecurityUser;
import com.skillforge.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<CompanyResponse> create(
            @Valid @RequestBody CompanyRequest request, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        CompanyResponse response = companyService.create(principal.getUser().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<CompanyResponse> mine(Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(companyService.getMine(principal.getUser().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<CompanyResponse> update(
            @PathVariable Long id, @Valid @RequestBody CompanyRequest request, Authentication authentication) {
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(companyService.update(principal.getUser().getId(), id, request));
    }
}
