package com.skillforge.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Stable, versioned JSON shape for every paginated endpoint. Wrapping Spring
 * Data's Page&lt;T&gt; here instead of returning it directly avoids depending on
 * PageImpl's own (HATEOAS-oriented, subject-to-change) Jackson serialization.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
