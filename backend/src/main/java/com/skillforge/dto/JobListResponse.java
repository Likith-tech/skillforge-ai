package com.skillforge.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobListResponse {

    private List<JobResponse> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}