package com.poc.chat.config;

import com.poc.chat.security.WebSocketAuthInterceptor;
import com.poc.chat.security.StompChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    private final StompChannelInterceptor stompChannelInterceptor;

    @Value("${app.websocket.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/user/queue")
                .setHeartbeatValue(new long[]{25000, 25000})
                .setTaskScheduler(heartbeatScheduler());
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    private TaskScheduler heartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Internal chat (authenticated users)
        registry.addEndpoint("/ws/chat")
                .setAllowedOrigins(allowedOrigins.split(","))
                .addInterceptors(webSocketAuthInterceptor)
                .withSockJS()
                .setHeartbeatTime(25000);

        // LiveChat widget (visitors via sessionToken)
        registry.addEndpoint("/ws/livechat")
                .setAllowedOrigins(allowedOrigins.split(","))
                .addInterceptors(webSocketAuthInterceptor)
                .withSockJS()
                .setHeartbeatTime(25000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompChannelInterceptor);
    }
}
