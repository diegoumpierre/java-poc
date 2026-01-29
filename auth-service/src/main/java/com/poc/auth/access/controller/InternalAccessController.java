package com.poc.auth.controller;

import com.poc.auth.client.UserClient;
import com.poc.auth.client.dto.PermissionDto;
import com.poc.auth.model.response.MembershipResponse;
import com.poc.auth.service.MembershipQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Internal endpoint for API Gateway to resolve permissions for a membership.
 * NOT exposed to frontend — called internally by gateway.
 * Now delegates permission resolution to user-service via Feign.
 */
@RestController
@RequestMapping("/api/auth/internal")
@RequiredArgsConstructor
@Slf4j
public class InternalAccessController {

    private final MembershipQueryService membershipQueryService;
    private final UserClient userClient;

    @GetMapping("/permissions")
    public ResponseEntity<Set<String>> getPermissions(@RequestParam UUID membershipId) {
        try {
            MembershipResponse membership = membershipQueryService.findById(membershipId);

            if (membership == null || membership.getRoleIds() == null || membership.getRoleIds().isEmpty()) {
                log.debug("No roles found for membership: {}", membershipId);
                return ResponseEntity.ok(Collections.emptySet());
            }

            List<UUID> roleIds = membership.getRoleIds();
            List<PermissionDto> permissions = userClient.findPermissionsByRoleIds(roleIds);
            Set<String> permissionCodes = permissions.stream()
                    .map(PermissionDto::getCode)
                    .collect(Collectors.toSet());

            log.debug("Resolved {} permissions for membership {}", permissionCodes.size(), membershipId);
            return ResponseEntity.ok(permissionCodes);
        } catch (Exception e) {
            log.error("Failed to resolve permissions for membership {}: {}", membershipId, e.getMessage());
            return ResponseEntity.ok(Collections.emptySet());
        }
    }
}
