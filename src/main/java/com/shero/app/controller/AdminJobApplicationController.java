package com.shero.app.controller;

import com.shero.app.dto.request.JobApplicationRequest;
import com.shero.app.dto.request.JobApplicationStatusRequest;
import com.shero.app.dto.response.JobApplicationResponse;
import com.shero.app.entity.enums.ApplicationStatus;
import com.shero.app.service.JobApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/job-applications")
@RequiredArgsConstructor
@Validated
public class AdminJobApplicationController {

    private final JobApplicationService jobApplicationService;

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getApplications(
            @RequestParam(required = false) @Positive Long userId,
            @RequestParam(required = false) ApplicationStatus status) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsForAdmin(userId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getApplicationById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(jobApplicationService.getApplicationByIdForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> createApplication(
            @RequestBody @Valid JobApplicationRequest request) {
        JobApplicationResponse created = jobApplicationService.createApplicationForAdmin(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @PathVariable @Positive Long id,
            @RequestBody @Valid JobApplicationStatusRequest request) {
        return ResponseEntity.ok(jobApplicationService.updateApplicationStatusForAdmin(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable @Positive Long id) {
        jobApplicationService.deleteApplicationForAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
