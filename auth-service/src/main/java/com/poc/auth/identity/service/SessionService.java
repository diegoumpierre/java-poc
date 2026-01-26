package com.poc.auth.service;

import com.poc.auth.client.UserClient;
import com.poc.auth.client.dto.InternalUserDto;
import com.poc.auth.client.dto.NotificationRequest;
import com.poc.auth.domain.UserSession;
import com.poc.auth.repository.UserSessionRepository;
import com.poc.auth.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    @Value("${app.security.session.max-concurrent:5}")
    private int maxConcurrentSessions;

    @Value("${app.security.session.notify-new-device:true}")
    private boolean notifyNewDevice;

    private final UserSessionRepository sessionRepository;
    private final UserClient userClient;
    private final TokenBlacklistService tokenBlacklistService;
    private final NotificationService notificationService;

    /**
     * Create a new session for a user
     */
    @Transactional
    public UserSession createSession(UUID userId, String tokenId, long expirationMs,
                                     String ipAddress, String userAgent) {
        // Check concurrent sessions limit
        enforceSessionLimit(userId);

        // Clear current flag from other sessions
        sessionRepository.clearCurrentFlag(userId);

        // Parse device info from user agent
        DeviceInfo deviceInfo = parseUserAgent(userAgent);

        UserSession session = UserSession.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tokenId(tokenId)
                .deviceName(deviceInfo.deviceName())
                .deviceType(deviceInfo.deviceType())
                .browser(deviceInfo.browser())
                .operatingSystem(deviceInfo.operatingSystem())
                .ipAddress(ipAddress)
                .isCurrent(true)
                .lastActivityAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(expirationMs))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        sessionRepository.save(session);

        // Check if this is a new device and notify user
        if (notifyNewDevice && isNewDevice(userId, deviceInfo, ipAddress)) {
            notifyNewDeviceLogin(userId, deviceInfo, ipAddress);
        }

        log.info("Session created for user {} from {} ({})", userId, ipAddress, deviceInfo.deviceType());
        return session;
    }

    /**
     * Get all active sessions for a user
     */
    public List<UserSession> getActiveSessions(UUID userId) {
        return sessionRepository.findActiveByUserId(userId);
    }

    /**
     * Revoke a specific session
     */
    @Transactional
    public void revokeSession(UUID userId, UUID sessionId) {
        sessionRepository.findById(sessionId)
                .filter(session -> session.getUserId().equals(userId))
                .ifPresent(session -> {
                    sessionRepository.revokeSession(sessionId, Instant.now(), "User revoked");
                    tokenBlacklistService.blacklistToken(session.getTokenId(), 0);
                    log.info("Session {} revoked by user {}", sessionId, userId);
                });
    }

    /**
     * Revoke all sessions except current one
     */
    @Transactional
    public void revokeOtherSessions(UUID userId, String currentTokenId) {
        List<UserSession> sessions = sessionRepository.findActiveByUserId(userId);
        for (UserSession session : sessions) {
            if (!session.getTokenId().equals(currentTokenId)) {
                sessionRepository.revokeSession(session.getId(), Instant.now(), "User logged out other sessions");
                tokenBlacklistService.blacklistToken(session.getTokenId(), 0);
            }
        }
        log.info("All other sessions revoked for user {}", userId);
    }

    /**
     * Revoke all user sessions (e.g., on password change)
     */
    @Transactional
    public void revokeAllSessions(UUID userId, String reason) {
        List<UserSession> sessions = sessionRepository.findActiveByUserId(userId);
        for (UserSession session : sessions) {
            tokenBlacklistService.blacklistToken(session.getTokenId(), 0);
        }
        sessionRepository.revokeAllUserSessions(userId, Instant.now(), reason);
        log.info("All sessions revoked for user {}: {}", userId, reason);
    }

    /**
     * Update session activity timestamp
     */
    @Transactional
    public void updateActivity(String tokenId) {
        sessionRepository.updateLastActivity(tokenId, Instant.now());
    }

    /**
     * Find session by token ID
     */
    public Optional<UserSession> findByTokenId(String tokenId) {
        return sessionRepository.findByTokenId(tokenId);
    }

    /**
     * Check if session is valid and not revoked
     */
    public boolean isSessionValid(String tokenId) {
        return sessionRepository.findByTokenId(tokenId)
                .map(UserSession::isActive)
                .orElse(false);
    }

    /**
     * Check if a session exists in DB and is explicitly revoked.
     * Returns false if session not found (token not tracked in sessions table).
     */
    public boolean isSessionRevoked(String tokenId) {
        return sessionRepository.findByTokenId(tokenId)
                .map(session -> Boolean.TRUE.equals(session.getRevoked()))
                .orElse(false);
    }

    /**
     * Enforce maximum concurrent sessions limit
     */
    private void enforceSessionLimit(UUID userId) {
        int activeSessions = sessionRepository.countActiveSessions(userId, Instant.now());

        if (activeSessions >= maxConcurrentSessions) {
            // Revoke oldest sessions to make room
            int toRevoke = activeSessions - maxConcurrentSessions + 1;
            List<UserSession> oldestSessions = sessionRepository.findOldestActiveSessions(
                    userId, Instant.now(), toRevoke);

            for (UserSession session : oldestSessions) {
                sessionRepository.revokeSession(session.getId(), Instant.now(),
                        "Session limit exceeded - oldest session revoked");
                tokenBlacklistService.blacklistToken(session.getTokenId(), 0);
                log.info("Revoked oldest session {} for user {} due to session limit", session.getId(), userId);
            }
        }
    }

    /**
     * Check if this is a new device for the user
     */
    private boolean isNewDevice(UUID userId, DeviceInfo deviceInfo, String ipAddress) {
        List<UserSession> recentSessions = sessionRepository.findActiveByUserId(userId);
        return recentSessions.stream()
                .noneMatch(session ->
                        deviceInfo.deviceType().equals(session.getDeviceType()) &&
                        deviceInfo.browser().equals(session.getBrowser()) &&
                        deviceInfo.operatingSystem().equals(session.getOperatingSystem()));
    }

    /**
     * Send notification about new device login
     */
    private void notifyNewDeviceLogin(UUID userId, DeviceInfo deviceInfo, String ipAddress) {
        try {
            InternalUserDto user = userClient.findById(userId);
            if (user != null) {
                String deviceDescription = String.format("%s on %s (%s)",
                        deviceInfo.browser(), deviceInfo.operatingSystem(), deviceInfo.deviceType());

                notificationService.send(NotificationRequest.newDeviceLogin(
                        user.getEmail(),
                        user.getName(),
                        deviceDescription,
                        ipAddress,
                        null // Location could be resolved via IP geolocation service
                ));

                log.info("New device login notification sent to user: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.warn("Failed to send new device login notification for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Parse user agent string to extract device information
     */
    private DeviceInfo parseUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return new DeviceInfo("Unknown Device", UserSession.DeviceType.UNKNOWN, "Unknown", "Unknown");
        }

        String deviceType = detectDeviceType(userAgent);
        String browser = detectBrowser(userAgent);
        String os = detectOperatingSystem(userAgent);
        String deviceName = String.format("%s on %s", browser, os);

        return new DeviceInfo(deviceName, deviceType, browser, os);
    }

    private String detectDeviceType(String userAgent) {
        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return UserSession.DeviceType.MOBILE;
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return UserSession.DeviceType.TABLET;
        } else if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux")) {
            return UserSession.DeviceType.DESKTOP;
        }
        return UserSession.DeviceType.UNKNOWN;
    }

    private String detectBrowser(String userAgent) {
        if (userAgent.contains("Chrome") && !userAgent.contains("Edg")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) return "Safari";
        if (userAgent.contains("Edg")) return "Edge";
        if (userAgent.contains("Opera") || userAgent.contains("OPR")) return "Opera";
        return "Unknown Browser";
    }

    private String detectOperatingSystem(String userAgent) {
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac OS X") || userAgent.contains("Macintosh")) return "macOS";
        if (userAgent.contains("Linux") && !userAgent.contains("Android")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        return "Unknown OS";
    }

    /**
     * Revoke ALL sessions across the entire platform (admin action)
     */
    @Transactional
    public int revokeAllPlatformSessions(String reason) {
        int count = sessionRepository.countAllActiveSessions();
        if (count > 0) {
            sessionRepository.revokeAllSessions(Instant.now(), reason);
            log.warn("Admin revoked ALL {} active sessions: {}", count, reason);
        }
        return count;
    }

    /**
     * Cleanup expired and revoked sessions (runs daily)
     */
    @Scheduled(fixedRate = 86400000) // 24 hours
    @Transactional
    public void cleanupExpiredSessions() {
        sessionRepository.deleteExpiredAndRevoked(Instant.now().minusSeconds(86400 * 7)); // Keep 7 days of history
        log.debug("Cleaned up expired sessions");
    }

    private record DeviceInfo(String deviceName, String deviceType, String browser, String operatingSystem) {}
}
