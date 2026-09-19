package com.shero.app.repository;

import com.shero.app.entity.Skill;
import com.shero.app.entity.enums.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
    List<Skill> findByCreatedById(Long createdById);
    List<Skill> findByCategory(SkillCategory category);
    List<Skill> findByCreatedByIdAndCategory(Long createdById, SkillCategory category);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
