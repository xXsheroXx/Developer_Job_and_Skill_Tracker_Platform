package com.shero.app.mapper;

import com.shero.app.dto.request.SkillRequest;
import com.shero.app.dto.response.SkillResponse;
import com.shero.app.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    SkillResponse toResponse(Skill skill);

    @Mapping(target = "id", ignore = true)
    Skill toEntity(SkillRequest request);
}
