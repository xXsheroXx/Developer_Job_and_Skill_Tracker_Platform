package com.shero.app.service;

import com.shero.app.dto.request.JobApplicationRequest;
import com.shero.app.dto.request.MyJobApplicationRequest;
import com.shero.app.dto.response.JobApplicationResponse;
import com.shero.app.entity.JobApplication;
import com.shero.app.entity.Skill;
import com.shero.app.entity.User;
import com.shero.app.entity.enums.ApplicationStatus;
import com.shero.app.entity.enums.Role;
import com.shero.app.exception.ResourceNotFoundException;
import com.shero.app.mapper.JobApplicationMapper;
import com.shero.app.repository.JobApplicationRepository;
import com.shero.app.repository.SkillRepository;
import com.shero.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationMapper jobApplicationMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public List<JobApplicationResponse> getMyApplications(ApplicationStatus status) {
        User currentUser = getCurrentAuthenticatedUser();
        List<JobApplication> applications = status == null
                ? jobApplicationRepository.findByUserId(currentUser.getId())
                : jobApplicationRepository.findByUserIdAndStatus(currentUser.getId(), status);
        return applications
                .stream()
                .map(jobApplicationMapper::toResponse)
                .toList();
    }

    public JobApplicationResponse getMyApplicationById(Long id) {
        JobApplication application = findApplicationById(id);
        ensureOwner(application);
        return jobApplicationMapper.toResponse(application);
    }

    @Transactional
    public JobApplicationResponse createMyApplication(MyJobApplicationRequest request) {
        User currentUser = getCurrentAuthenticatedUser();
        List<Skill> skills = resolveSkills(request.skillIds());
        ensureSkillsBelongToUser(skills, currentUser.getId());
        JobApplication application = buildApplication(
                request.companyName(),
                request.jobTitle(),
                request.jobDescription(),
                request.jobUrl(),
                request.status(),
                request.appliedAt(),
                request.respondedAt(),
                request.notes(),
                currentUser,
                skills
        );
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public JobApplicationResponse updateMyApplicationStatus(Long id, ApplicationStatus status) {
        JobApplication application = findApplicationById(id);
        ensureOwner(application);
        application.setStatus(status);
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public void deleteMyApplication(Long id) {
        JobApplication application = findApplicationById(id);
        ensureOwner(application);
        deleteById(id);
    }

    public List<JobApplicationResponse> getApplicationsForAdmin(Long userId, ApplicationStatus status) {
        ensureAdmin();
        List<JobApplication> applications;
        if (userId != null && status != null) {
            applications = jobApplicationRepository.findByUserIdAndStatus(userId, status);
        } else if (userId != null) {
            applications = jobApplicationRepository.findByUserId(userId);
        } else if (status != null) {
            applications = jobApplicationRepository.findByStatus(status);
        } else {
            applications = jobApplicationRepository.findAll();
        }
        return applications.stream().map(jobApplicationMapper::toResponse).toList();
    }

    public JobApplicationResponse getApplicationByIdForAdmin(Long id) {
        ensureAdmin();
        return jobApplicationMapper.toResponse(findApplicationById(id));
    }

    @Transactional
    public JobApplicationResponse createApplicationForAdmin(JobApplicationRequest request) {
        ensureAdmin();
        User user = findUserById(request.userId());
        List<Skill> skills = resolveSkills(request.skillIds());
        JobApplication application = buildApplication(
                request.companyName(),
                request.jobTitle(),
                request.jobDescription(),
                request.jobUrl(),
                request.status(),
                request.appliedAt(),
                request.respondedAt(),
                request.notes(),
                user,
                skills
        );
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public JobApplicationResponse updateApplicationStatusForAdmin(Long id, ApplicationStatus status) {
        ensureAdmin();
        JobApplication application = findApplicationById(id);
        application.setStatus(status);
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public void deleteApplicationForAdmin(Long id) {
        ensureAdmin();
        findApplicationById(id);
        deleteById(id);
    }

    private JobApplication buildApplication(
            String companyName,
            String jobTitle,
            String jobDescription,
            String jobUrl,
            ApplicationStatus status,
            LocalDate appliedAt,
            LocalDate respondedAt,
            String notes,
            User user,
            List<Skill> skills
    ) {
        return JobApplication.builder()
                .companyName(companyName)
                .jobTitle(jobTitle)
                .jobDescription(jobDescription)
                .jobUrl(jobUrl)
                .status(status)
                .appliedAt(appliedAt != null ? appliedAt : LocalDate.now())
                .respondedAt(respondedAt)
                .notes(notes)
                .user(user)
                .skills(skills)
                .build();
    }

    private List<Skill> resolveSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return List.of();
        }
        List<Skill> skills = skillRepository.findAllById(skillIds);
        if (skills.size() != skillIds.size()) {
            throw new ResourceNotFoundException("One or more provided skills do not exist.");
        }
        return skills;
    }

    private void ensureSkillsBelongToUser(List<Skill> skills, Long userId) {
        boolean hasForeignSkill = skills.stream()
                .anyMatch(skill -> skill.getCreatedBy() == null
                        || !userId.equals(skill.getCreatedBy().getId()));
        if (hasForeignSkill) {
            throw new AccessDeniedException("You can only attach your own skills to your job applications.");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private JobApplication findApplicationById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
    }

    private void ensureOwner(JobApplication application) {
        User currentUser = getCurrentAuthenticatedUser();
        if (!currentUser.getId().equals(application.getUser().getId())) {
            throw new AccessDeniedException("You can only access your own job applications.");
        }
    }

    private void ensureAdmin() {
        if (!isAdmin(getCurrentAuthenticatedUser())) {
            throw new AccessDeniedException("Only admins can access this resource.");
        }
    }

    private void deleteById(Long id) {
        int deletedCount = jobApplicationRepository.deleteJobApplicationById(id);
        if (deletedCount == 0) {
            throw new ResourceNotFoundException("Job application not found with id: " + id);
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
