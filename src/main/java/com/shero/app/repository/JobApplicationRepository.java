package com.shero.app.repository;

import com.shero.app.entity.JobApplication;
import com.shero.app.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status);

    @Modifying
    @Query("DELETE FROM JobApplication j WHERE j.id = :id")
    int deleteJobApplicationById(@Param("id") Long id);
}
