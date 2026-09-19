package com.shero.app.controller;

import com.shero.app.dto.request.JobApplicationStatusRequest;
import com.shero.app.dto.request.MyJobApplicationRequest;
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
@RequestMapping("/api/v1/me/job-applications")
@RequiredArgsConstructor
@Validated
public class MeJobApplicationController {

    private final JobApplicationService jobApplicationService;

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getMyApplications(
            @RequestParam(required = false) ApplicationStatus status) {
        return ResponseEntity.ok(jobApplicationService.getMyApplications(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getMyApplicationById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(jobApplicationService.getMyApplicationById(id));
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> createMyApplication(
            @RequestBody @Valid MyJobApplicationRequest request) {
        JobApplicationResponse created = jobApplicationService.createMyApplication(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateMyApplicationStatus(
            @PathVariable @Positive Long id,
            @RequestBody @Valid JobApplicationStatusRequest request) {
        return ResponseEntity.ok(jobApplicationService.updateMyApplicationStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyApplication(@PathVariable @Positive Long id) {
        jobApplicationService.deleteMyApplication(id);
        return ResponseEntity.noContent().build();
    }
}
