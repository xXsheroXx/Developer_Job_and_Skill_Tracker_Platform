package com.shero.app.dto.response;

import com.shero.app.entity.enums.ApplicationStatus;

import java.time.LocalDate;
import java.util.List;

public record JobApplicationResponse(
        Long id,
        String companyName,
        String jobTitle,
        String jobDescription,
        String jobUrl,
        ApplicationStatus status,
        LocalDate appliedAt,
        LocalDate respondedAt,
        String notes,
        Long userId,
        List<SkillResponse> skills
) {}
