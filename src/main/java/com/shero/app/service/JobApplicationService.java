package com.shero.app.service;

import com.shero.app.entity.JobApplication;
import com.shero.app.entity.enums.ApplicationStatus;
import com.shero.app.exception.ResourceNotFoundException;
import com.shero.app.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public List<JobApplication> getApplicationsByUserId(Long userId) {
        return jobApplicationRepository.findByUserId(userId);
    }

    public List<JobApplication> getApplicationsByUserIdAndStatus(Long userId, ApplicationStatus status) {
        return jobApplicationRepository.findByUserIdAndStatus(userId, status);
    }

    public JobApplication getApplicationById(Long id) {
        return jobApplicationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
    }

    public JobApplication createApplication(JobApplication application) {
        return jobApplicationRepository.save(application);
    }

    public JobApplication updateApplicationStatus(Long id, ApplicationStatus status) {
        JobApplication application = getApplicationById(id);
        application.setStatus(status);
        return jobApplicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        if (!jobApplicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job application not found with id: " + id);
        }
        jobApplicationRepository.deleteById(id);
    }
}
