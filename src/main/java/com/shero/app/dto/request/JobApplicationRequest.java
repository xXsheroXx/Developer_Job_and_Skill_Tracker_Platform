package com.shero.app.dto.request;

import com.shero.app.entity.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record JobApplicationRequest(
        @NotBlank String companyName,
        @NotBlank String jobTitle,
        String jobDescription,
        String jobUrl,
        @NotNull ApplicationStatus status,
        LocalDate appliedAt,
        LocalDate respondedAt,
        String notes,
        @NotNull Long userId,
        List<Long> skillIds
) {}
