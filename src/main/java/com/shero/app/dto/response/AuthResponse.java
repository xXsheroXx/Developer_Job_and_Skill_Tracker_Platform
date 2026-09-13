package com.shero.app.dto.response;

import com.shero.app.entity.enums.Role;

public record AuthResponse(
        String token,
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role
) {}
