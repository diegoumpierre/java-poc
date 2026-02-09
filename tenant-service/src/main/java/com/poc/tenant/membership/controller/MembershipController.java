package com.poc.tenant.membership.controller;

import com.poc.tenant.membership.model.MembershipRequest;
import com.poc.tenant.membership.model.MembershipResponse;
import com.poc.tenant.membership.service.MembershipService;
import com.poc.tenant.security.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/memberships")
@RequiredArgsConstructor
@Tag(name = "Memberships", description = "Membership management")
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping
    @Operation(summary = "List all memberships")
    public ResponseEntity<List<MembershipResponse>> listAll() {
        return ResponseEntity.ok(membershipService.findAll());
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's memberships")
    public ResponseEntity<List<MembershipResponse>> getMyMemberships() {
        UUID userId = TenantContext.getUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(membershipService.findByUserId(userId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get memberships by user ID")
    public ResponseEntity<List<MembershipResponse>> getMembershipsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(membershipService.findByUserId(userId));
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get memberships by tenant ID")
    public ResponseEntity<List<MembershipResponse>> getMembershipsByTenant(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(membershipService.findByTenantId(tenantId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get membership by ID")
    public ResponseEntity<MembershipResponse> getMembershipById(@PathVariable UUID id) {
        return ResponseEntity.ok(membershipService.findById(id));
    }

    @PostMapping("/tenant/{tenantId}")
    @Operation(summary = "Add member to organization")
    public ResponseEntity<MembershipResponse> addMember(
            @PathVariable UUID tenantId,
            @Valid @RequestBody MembershipRequest request) {
        return ResponseEntity.ok(membershipService.addMember(tenantId, request));
    }

    @DeleteMapping("/tenant/{tenantId}/user/{userId}")
    @Operation(summary = "Remove member from organization")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId) {
        membershipService.removeMember(tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{membershipId}/roles")
    @Operation(summary = "Update member roles")
    public ResponseEntity<MembershipResponse> updateRoles(
            @PathVariable UUID membershipId,
            @RequestBody List<UUID> roleIds) {
        return ResponseEntity.ok(membershipService.updateRoles(membershipId, roleIds));
    }
}
