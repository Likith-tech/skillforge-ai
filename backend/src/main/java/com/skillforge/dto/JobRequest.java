package com.skillforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Company is required")
    private String company;

    private String location;

    @NotNull(message = "Job type is required")
    private String type;

    /** Optional: ENTRY | MID | SENIOR | LEAD. Omit if not specified. */
    private String experienceLevel;

    /** Optional free-text salary range, e.g. "8-12 LPA" or "$80k-$100k". */
    private String salaryRange;

    @NotEmpty(message = "At least one required skill must be listed")
    private List<String> requiredSkills;
}
