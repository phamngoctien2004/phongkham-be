package com.dcm.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtStompInterceptor implements ExecutorChannelInterceptor {
    private final AuthenticationManager jwtAuthManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null)
            return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    // Authenticate token
                    Authentication preAuth = new BearerTokenAuthenticationToken(token);
                    Authentication auth = jwtAuthManager.authenticate(preAuth);

                    // Set user for WebSocket message
                    accessor.setUser(auth);

                    // Lưu authentication vào message header để truyền sang thread khác
                    accessor.setHeader("simpAuthentication", auth);

                    log.info("✅ WebSocket authentication successful for user: {} (Command: {}, Thread: {})",
                            auth.getName(), accessor.getCommand(), Thread.currentThread().getName());
                } catch (Exception e) {
                    log.error("❌ WebSocket authentication failed: {}", e.getMessage(), e);
                }
            }
        }
        return message;
    }

    @Override
    public Message<?> beforeHandle(Message<?> message, MessageChannel channel,
            org.springframework.messaging.MessageHandler handler) {
        // Được gọi trong thread của message handler - SAU KHI thread switch
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            Authentication auth = (Authentication) accessor.getHeader("simpAuthentication");
            if (auth != null) {
                // Set SecurityContext cho thread này
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("✅ Set authentication in handler thread: {} (Thread: {})",
                        auth.getName(), Thread.currentThread().getName());
            }
        }
        return message;
    }

    @Override
    public void afterMessageHandled(Message<?> message, MessageChannel channel,
            org.springframework.messaging.MessageHandler handler,
            Exception ex) {
        // Clear SecurityContext sau khi handler xong
        SecurityContextHolder.clearContext();
        log.debug("🧹 Cleared SecurityContext after handler (Thread: {})",
                Thread.currentThread().getName());
    }
}