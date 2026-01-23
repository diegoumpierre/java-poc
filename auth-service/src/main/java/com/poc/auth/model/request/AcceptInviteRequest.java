package com.poc.auth.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptInviteRequest {

    @NotBlank(message = "Invite code is required")
    private String code;

    // If user doesn't exist, these are required
    private String name;
    private String password;
}
