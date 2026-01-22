package com.poc.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLoginStateRequest {
    private Integer failedLoginAttempts;
    private Instant lockedUntil;
    private Boolean resetAttempts;
}
