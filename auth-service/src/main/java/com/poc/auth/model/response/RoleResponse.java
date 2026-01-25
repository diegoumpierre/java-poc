package com.poc.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private UUID id;
    private String code;
    private String name;
    private boolean isSystem;
    private UUID productId;
    private UUID tenantId;
    private List<PermissionResponse> permissions;
    private Instant createdAt;
    private Instant updatedAt;
}
