package com.skillforge.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResponse {

    private List<UserResponse> students;
    private List<UserResponse> recruiters;
    private List<JobResponse> jobs;
    private List<CompanyResponse> companies;
    private List<String> skills;
}