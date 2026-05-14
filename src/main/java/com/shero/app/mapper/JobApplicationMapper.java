package com.shero.app.mapper;

import com.shero.app.dto.response.JobApplicationResponse;
import com.shero.app.dto.response.SkillResponse;
import com.shero.app.entity.JobApplication;
import com.shero.app.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    @Mapping(source = "user.id", target = "userId")
    JobApplicationResponse toResponse(JobApplication application);

    SkillResponse toSkillResponse(Skill skill);
}
