package com.shero.app.service;

import com.shero.app.dto.request.SkillRequest;
import com.shero.app.dto.response.SkillResponse;
import com.shero.app.entity.Skill;
import com.shero.app.entity.User;
import com.shero.app.entity.enums.Role;
import com.shero.app.entity.enums.SkillCategory;
import com.shero.app.exception.ResourceNotFoundException;
import com.shero.app.mapper.SkillMapper;
import com.shero.app.repository.SkillRepository;
import com.shero.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;

    public List<SkillResponse> getMySkills(String search, SkillCategory category) {
        User currentUser = getCurrentAuthenticatedUser();
        List<Skill> skills = category == null
                ? skillRepository.findByCreatedById(currentUser.getId())
                : skillRepository.findByCreatedByIdAndCategory(currentUser.getId(), category);
        return mapFilteredSkills(skills, search);
    }

    public SkillResponse getMySkillById(Long id) {
        return skillMapper.toResponse(findOwnedSkill(id));
    }

    @Transactional
    public SkillResponse createMySkill(SkillRequest request) {
        User currentUser = getCurrentAuthenticatedUser();
        String normalizedName = normalizeName(request.name());
        ensureUniqueNameForCreate(normalizedName);
        Skill skill = skillMapper.toEntity(request);
        skill.setName(normalizedName);
        skill.setCreatedBy(currentUser);
        return skillMapper.toResponse(skillRepository.save(skill));
    }

    @Transactional
    public SkillResponse updateMySkill(Long id, SkillRequest request) {
        Skill skill = findOwnedSkill(id);
        String normalizedName = normalizeName(request.name());
        ensureUniqueNameForUpdate(normalizedName, id);
        skillMapper.updateEntityFromRequest(request, skill);
        skill.setName(normalizedName);
        return skillMapper.toResponse(skillRepository.save(skill));
    }

    @Transactional
    public void deleteMySkill(Long id) {
        skillRepository.delete(findOwnedSkill(id));
    }

    public List<SkillResponse> getAllSkillsForAdmin(String search, SkillCategory category) {
        ensureAdmin();
        List<Skill> skills = category == null
                ? skillRepository.findAll()
                : skillRepository.findByCategory(category);
        return mapFilteredSkills(skills, search);
    }

    public SkillResponse getSkillByIdForAdmin(Long id) {
        ensureAdmin();
        return skillMapper.toResponse(findSkillById(id));
    }

    @Transactional
    public SkillResponse createSkillForAdmin(SkillRequest request) {
        ensureAdmin();
        return createMySkill(request);
    }

    @Transactional
    public SkillResponse updateSkillForAdmin(Long id, SkillRequest request) {
        ensureAdmin();
        Skill skill = findSkillById(id);
        String normalizedName = normalizeName(request.name());
        ensureUniqueNameForUpdate(normalizedName, id);
        skillMapper.updateEntityFromRequest(request, skill);
        skill.setName(normalizedName);
        return skillMapper.toResponse(skillRepository.save(skill));
    }

    @Transactional
    public void deleteSkillForAdmin(Long id) {
        ensureAdmin();
        skillRepository.delete(findSkillById(id));
    }

    private List<SkillResponse> mapFilteredSkills(List<Skill> skills, String search) {
        String normalizedSearch = search == null ? null : search.trim().toLowerCase(Locale.ROOT);
        if (normalizedSearch != null && !normalizedSearch.isBlank()) {
            skills = skills.stream()
                    .filter(skill -> skill.getName() != null
                            && skill.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                    .toList();
        }
        return skills.stream().map(skillMapper::toResponse).toList();
    }

    private Skill findOwnedSkill(Long id) {
        User currentUser = getCurrentAuthenticatedUser();
        Skill skill = findSkillById(id);
        boolean isOwner = skill.getCreatedBy() != null
                && currentUser.getId().equals(skill.getCreatedBy().getId());
        if (!isOwner) {
            throw new AccessDeniedException("You can only access your own skills.");
        }
        return skill;
    }

    private Skill findSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
    }

    private void ensureUniqueNameForCreate(String name) {
        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Skill name already exists: " + name);
        }
    }

    private void ensureUniqueNameForUpdate(String name, Long id) {
        if (skillRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new IllegalArgumentException("Skill name already exists: " + name);
        }
    }

    private String normalizeName(String name) {
        return name.trim();
    }

    private void ensureAdmin() {
        if (!isAdmin(getCurrentAuthenticatedUser())) {
            throw new AccessDeniedException("Only admins can access this resource.");
        }
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Authentication required.");
        }

        Object principal = authentication.getPrincipal();
        String email = principal instanceof UserDetails userDetails
                ? userDetails.getUsername()
                : principal.toString();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));
    }
}