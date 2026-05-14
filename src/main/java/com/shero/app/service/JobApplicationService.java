package com.shero.app.service;

import com.shero.app.dto.request.JobApplicationRequest;
import com.shero.app.dto.response.JobApplicationResponse;
import com.shero.app.entity.JobApplication;
import com.shero.app.entity.Skill;
import com.shero.app.entity.User;
import com.shero.app.entity.enums.ApplicationStatus;
import com.shero.app.exception.ResourceNotFoundException;
import com.shero.app.mapper.JobApplicationMapper;
import com.shero.app.repository.JobApplicationRepository;
import com.shero.app.repository.SkillRepository;
import com.shero.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public List<JobApplicationResponse> getApplicationsByUserId(Long userId) {
        return jobApplicationRepository.findByUserId(userId)
                .stream()
                .map(jobApplicationMapper::toResponse)
                .toList();
    }

    public List<JobApplicationResponse> getApplicationsByUserIdAndStatus(Long userId, ApplicationStatus status) {
        return jobApplicationRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(jobApplicationMapper::toResponse)
                .toList();
    }

    public JobApplicationResponse getApplicationById(Long id) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
        return jobApplicationMapper.toResponse(application);
    }

    @Transactional
    public JobApplicationResponse createApplication(JobApplicationRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));

        List<Skill> skills = skillRepository.findAllById(request.skillIds());

        // TODO: replace with a more specific BadRequestException
        if (skills.size() != request.skillIds().size()) {
            throw new ResourceNotFoundException("One or more provided skills do not exist.");
        }

        JobApplication application = JobApplication.builder()
                .companyName(request.companyName())
                .jobTitle(request.jobTitle())
                .jobDescription(request.jobDescription())
                .jobUrl(request.jobUrl())
                .status(request.status())
                .appliedAt(request.appliedAt() != null ? request.appliedAt() : LocalDate.now())
                .respondedAt(request.respondedAt())
                .notes(request.notes())
                .user(user)
                .skills(skills)
                .build();

        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public JobApplicationResponse updateApplicationStatus(Long id, ApplicationStatus status) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
        application.setStatus(status);
        return jobApplicationMapper.toResponse(jobApplicationRepository.save(application));
    }

    @Transactional
    public void deleteApplication(Long id) {
        int deletedCount = jobApplicationRepository.deleteJobApplicationById(id);
        if (deletedCount == 0) {
            throw new ResourceNotFoundException("Job application not found with id: " + id);
        }
    }
}
