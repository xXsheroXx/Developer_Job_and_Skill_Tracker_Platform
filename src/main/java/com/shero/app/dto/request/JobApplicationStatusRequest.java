package com.shero.app.dto.request;

import com.shero.app.entity.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record JobApplicationStatusRequest(
        @NotNull ApplicationStatus status
) {
}
