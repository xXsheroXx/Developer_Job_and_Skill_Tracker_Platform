package com.shero.app.dto.response;

import com.shero.app.entity.enums.SkillCategory;

public record SkillResponse(
        Long id,
        String name,
        SkillCategory category
) {
}
