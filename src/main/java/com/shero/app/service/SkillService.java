package com.shero.app.service;

import com.shero.app.dto.request.SkillRequest;
import com.shero.app.dto.response.SkillResponse;
import com.shero.app.entity.Skill;
import com.shero.app.exception.ResourceNotFoundException;
import com.shero.app.mapper.SkillMapper;
import com.shero.app.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    public SkillResponse getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        return skillMapper.toResponse(skill);
    }

    @Transactional
    public SkillResponse createSkill(SkillRequest request) {
        Skill skill = skillMapper.toEntity(request);
        return skillMapper.toResponse(skillRepository.save(skill));
    }

    @Transactional
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        skillRepository.delete(skill);
    }
}