package com.shero.app.controller;

import com.shero.app.dto.request.SkillRequest;
import com.shero.app.dto.response.SkillResponse;
import com.shero.app.entity.enums.SkillCategory;
import com.shero.app.service.SkillService;
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
@RequestMapping("/api/v1/admin/skills")
@RequiredArgsConstructor
@Validated
public class AdminSkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SkillCategory category) {
        return ResponseEntity.ok(skillService.getAllSkillsForAdmin(search, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getSkillById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(skillService.getSkillByIdForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(@RequestBody @Valid SkillRequest request) {
        SkillResponse created = skillService.createSkillForAdmin(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable @Positive Long id,
            @RequestBody @Valid SkillRequest request) {
        return ResponseEntity.ok(skillService.updateSkillForAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable @Positive Long id) {
        skillService.deleteSkillForAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
