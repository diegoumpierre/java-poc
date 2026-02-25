package com.poc.chat.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenValidator tokenValidator;
    private final JdbcClient jdbcClient;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            // Try token from query param first (WebSocket standard)
            String token = servletRequest.getServletRequest().getParameter("token");

            // Fallback: try headers (gateway forwarded)
            if (token == null || token.isBlank()) {
                String userIdHeader = servletRequest.getServletRequest().getHeader("X-User-Id");
                String tenantIdHeader = servletRequest.getServletRequest().getHeader("X-Tenant-Id");

                if (userIdHeader != null && tenantIdHeader != null) {
                    attributes.put("userId", userIdHeader);
                    attributes.put("tenantId", tenantIdHeader);
                    log.debug("WebSocket handshake via gateway headers: userId={}, tenantId={}", userIdHeader, tenantIdHeader);
                    return true;
                }
            }

            // Try session token (visitor livechat connection)
            String sessionToken = servletRequest.getServletRequest().getParameter("sessionToken");
            if (sessionToken != null && !sessionToken.isBlank()) {
                try {
                    var result = jdbcClient.sql("""
                            SELECT SESSION_TOKEN, TENANT_ID, VISITOR_ID, STATUS
                            FROM CHAT_LIVECHAT_SESSION
                            WHERE SESSION_TOKEN = :sessionToken
                            AND STATUS IN ('WAITING', 'ACTIVE')
                            """)
                            .param("sessionToken", sessionToken)
                            .query((rs, rowNum) -> Map.of(
                                    "sessionToken", rs.getString("SESSION_TOKEN"),
                                    "tenantId", rs.getString("TENANT_ID"),
                                    "visitorId", String.valueOf(rs.getLong("VISITOR_ID"))
                            ))
                            .optional();

                    if (result.isPresent()) {
                        Map<String, String> sessionData = result.get();
                        attributes.put("sessionToken", sessionData.get("sessionToken"));
                        attributes.put("tenantId", sessionData.get("tenantId"));
                        attributes.put("visitorId", sessionData.get("visitorId"));
                        attributes.put("isVisitor", "true");
                        log.debug("WebSocket handshake via session token: visitorId={}, tenantId={}",
                                sessionData.get("visitorId"), sessionData.get("tenantId"));
                        return true;
                    } else {
                        log.warn("WebSocket handshake rejected: invalid or inactive session token");
                    }
                } catch (Exception e) {
                    log.error("Error validating session token for WebSocket handshake: {}", e.getMessage());
                }
            }

            if (token != null && !token.isBlank() && tokenValidator.validateToken(token)) {
                UUID userId = tokenValidator.getUserIdFromToken(token);
                String tenantId = tokenValidator.getTenantIdFromToken(token);

                attributes.put("userId", userId.toString());
                if (tenantId != null) {
                    attributes.put("tenantId", tenantId);
                }
                log.debug("WebSocket handshake authenticated: userId={}", userId);
                return true;
            }
        }

        log.warn("WebSocket handshake rejected: no valid authentication");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // No-op
    }
}
