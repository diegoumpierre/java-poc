package com.poc.tenant.membership.controller;

import com.poc.tenant.membership.model.MembershipRequest;
import com.poc.tenant.membership.model.MembershipResponse;
import com.poc.tenant.membership.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/internal/memberships")
@RequiredArgsConstructor
@Tag(name = "Internal Memberships", description = "Internal membership endpoints for service-to-service calls")
public class InternalMembershipController {

    private final MembershipService membershipService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get memberships by user ID (for auth-service)")
    public ResponseEntity<List<MembershipResponse>> getMembershipsByUserId(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(membershipService.findByUserId(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get membership by ID (for auth-service)")
    public ResponseEntity<MembershipResponse> getMembershipById(@PathVariable UUID id) {
        return ResponseEntity.ok(membershipService.findById(id));
    }

    @PostMapping("/tenant/{tenantId}")
    @Operation(summary = "Create membership (for billing-service provisioning)")
    public ResponseEntity<MembershipResponse> createMembership(
            @PathVariable UUID tenantId,
            @Valid @RequestBody MembershipRequest request) {
        return ResponseEntity.ok(membershipService.addMember(tenantId, request));
    }
}
