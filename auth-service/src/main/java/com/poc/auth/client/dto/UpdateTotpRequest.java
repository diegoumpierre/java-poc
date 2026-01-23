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
public class UpdateTotpRequest {
    private String totpSecret;
    private Instant totpVerifiedAt;
    private Boolean twoFactorEnabled;
    private String twoFactorMethod;
}
