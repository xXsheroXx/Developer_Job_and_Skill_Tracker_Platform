package com.shero.app.controller;

import com.shero.app.dto.response.UserResponse;
import com.shero.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMyAccount() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
