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
@RequestMapping("/api/v1/me/skills")
@RequiredArgsConstructor
@Validated
public class MeSkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getMySkills(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SkillCategory category) {
        return ResponseEntity.ok(skillService.getMySkills(search, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getMySkillById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(skillService.getMySkillById(id));
    }

    @PostMapping
    public ResponseEntity<SkillResponse> createMySkill(@RequestBody @Valid SkillRequest request) {
        SkillResponse created = skillService.createMySkill(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateMySkill(
            @PathVariable @Positive Long id,
            @RequestBody @Valid SkillRequest request) {
        return ResponseEntity.ok(skillService.updateMySkill(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMySkill(@PathVariable @Positive Long id) {
        skillService.deleteMySkill(id);
        return ResponseEntity.noContent().build();
    }
}
