package com.poc.tenant.model.response;

import com.poc.tenant.tenant.domain.TenantInvite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteResponse {
    private UUID id;
    private UUID tenantId;
    private String email;
    private String code;
    private String status;
    private UUID invitedBy;
    private Instant expiresAt;
    private Instant createdAt;

    public static InviteResponse from(TenantInvite invite) {
        return InviteResponse.builder()
                .id(invite.getId())
                .tenantId(invite.getTenantId())
                .email(invite.getEmail())
                .code(invite.getCode())
                .status(invite.getStatus())
                .invitedBy(invite.getInvitedBy())
                .expiresAt(invite.getExpiresAt())
                .createdAt(invite.getCreatedAt())
                .build();
    }
}
