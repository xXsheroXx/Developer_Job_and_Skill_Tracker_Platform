package com.shero.app.repository;

import com.shero.app.entity.JobApplication;
import com.shero.app.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(Long userId);
    List<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status);
}
