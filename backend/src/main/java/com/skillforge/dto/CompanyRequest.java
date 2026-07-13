package com.skillforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 150, message = "Company name must be at most 150 characters")
    private String companyName;

    private String description;

    @Size(max = 255, message = "Website must be at most 255 characters")
    private String website;

    @Size(max = 150, message = "Location must be at most 150 characters")
    private String location;

    @Size(max = 500, message = "Logo URL must be at most 500 characters")
    private String logo;
}
