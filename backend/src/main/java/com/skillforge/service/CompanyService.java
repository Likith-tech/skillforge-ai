package com.skillforge.service;

import com.skillforge.dto.CompanyRequest;
import com.skillforge.dto.CompanyResponse;

public interface CompanyService {

    CompanyResponse create(Long ownerId, CompanyRequest request);

    CompanyResponse getById(Long id);

    CompanyResponse getMine(Long ownerId);

    CompanyResponse update(Long ownerId, Long id, CompanyRequest request);
}
