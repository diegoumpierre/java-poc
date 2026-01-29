package com.poc.auth.controller;

import com.poc.auth.model.response.AuthResponse;
import com.poc.auth.model.response.SessionResponse;
import com.poc.auth.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Session management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SessionController {

    private final SessionService sessionService;

    /**
     * GET /api/sessions
     * Get all active sessions for the current user
     */
    @GetMapping
    @Operation(summary = "Get active sessions", description = "Get all active sessions for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<SessionResponse>> getActiveSessions(
            @RequestHeader("X-User-Id") String userId) {

        List<SessionResponse> sessions = sessionService.getActiveSessions(UUID.fromString(userId))
                .stream()
                .map(SessionResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sessions);
    }

    /**
     * DELETE /api/sessions/{sessionId}
     * Revoke a specific session
     */
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Revoke session", description = "Revoke a specific session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session revoked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<AuthResponse> revokeSession(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID sessionId) {

        sessionService.revokeSession(UUID.fromString(userId), sessionId);
        return ResponseEntity.ok(AuthResponse.success("Session revoked successfully"));
    }

    /**
     * DELETE /api/sessions/others
     * Revoke all sessions except the current one
     */
    @DeleteMapping("/others")
    @Operation(summary = "Revoke other sessions", description = "Revoke all sessions except the current one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Other sessions revoked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<AuthResponse> revokeOtherSessions(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String tokenId = extractTokenId(authHeader);
        if (tokenId != null) {
            sessionService.revokeOtherSessions(UUID.fromString(userId), tokenId);
        }
        return ResponseEntity.ok(AuthResponse.success("Other sessions revoked successfully"));
    }

    /**
     * DELETE /api/sessions/all
     * Revoke all sessions (logout from all devices)
     */
    @DeleteMapping("/all")
    @Operation(summary = "Revoke all sessions", description = "Revoke all sessions including current one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All sessions revoked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<AuthResponse> revokeAllSessions(
            @RequestHeader("X-User-Id") String userId) {

        sessionService.revokeAllSessions(UUID.fromString(userId), "User logged out from all devices");
        return ResponseEntity.ok(AuthResponse.success("All sessions revoked successfully. Please log in again."));
    }

    /**
     * DELETE /api/sessions/admin/revoke-all
     * Revoke ALL sessions across the entire platform (admin only)
     */
    @DeleteMapping("/admin/revoke-all")
    @Operation(summary = "Revoke all platform sessions", description = "Revoke all active sessions for all users (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All sessions revoked successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<AuthResponse> revokeAllPlatformSessions(
            @RequestHeader("X-User-Id") String userId) {

        int count = sessionService.revokeAllPlatformSessions(
                "Admin revoke-all by user " + userId);
        return ResponseEntity.ok(AuthResponse.success(
                "All " + count + " active sessions revoked. All users must log in again."));
    }

    private String extractTokenId(String authHeader) {
        // Token ID extraction would need to be done via JwtTokenProvider
        // For now, return null - this would need integration with JWT service
        return null;
    }
}
