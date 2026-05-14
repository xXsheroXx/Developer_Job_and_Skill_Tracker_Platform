package com.shero.app.controller;

import com.shero.app.dto.request.JobApplicationRequest;
import com.shero.app.dto.request.JobApplicationStatusRequest;
import com.shero.app.dto.response.JobApplicationResponse;
import com.shero.app.entity.enums.ApplicationStatus;
import com.shero.app.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByUserId(userId));
    }

    @GetMapping("/user/{userId}/status")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsByStatus(
            @PathVariable Long userId,
            @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsByUserIdAndStatus(userId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getApplicationById(id));
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(
            @RequestBody @Valid JobApplicationRequest request) {
        JobApplicationResponse created = jobApplicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid JobApplicationStatusRequest request) {
        JobApplicationResponse updated = jobApplicationService.updateApplicationStatus(id, request.status());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
            jobApplicationService.deleteApplication(id);
            return ResponseEntity.noContent().build();
    }
}
