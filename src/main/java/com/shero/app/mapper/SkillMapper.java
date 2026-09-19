package com.shero.app.mapper;

import com.shero.app.dto.request.SkillRequest;
import com.shero.app.dto.response.SkillResponse;
import com.shero.app.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(source = "createdBy.id", target = "createdById")
    SkillResponse toResponse(Skill skill);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Skill toEntity(SkillRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntityFromRequest(SkillRequest request, @MappingTarget Skill skill);
}
