package com.skillforge.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Clamps client-supplied page/size into sane bounds so nobody can request page=-1 or size=100000. */
public final class PageRequestFactory {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PageRequestFactory() {
    }

    public static Pageable of(Integer page, Integer size, Sort sort) {
        int safePage = (page == null || page < 0) ? 0 : page;
        int safeSize = (size == null || size <= 0) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        return PageRequest.of(safePage, safeSize, sort);
    }
}
