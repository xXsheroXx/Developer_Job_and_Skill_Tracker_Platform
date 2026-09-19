package com.shero.app.dto.request;

import com.shero.app.entity.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record MyJobApplicationRequest(
        @NotBlank String companyName,
        @NotBlank String jobTitle,
        String jobDescription,
        String jobUrl,
        @NotNull ApplicationStatus status,
        LocalDate appliedAt,
        LocalDate respondedAt,
        String notes,
        List<Long> skillIds
) {}
