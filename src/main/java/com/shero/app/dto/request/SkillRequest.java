package com.shero.app.dto.request;

import com.shero.app.entity.enums.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SkillRequest(
        @NotBlank String name,
        @NotNull SkillCategory category
) {}
