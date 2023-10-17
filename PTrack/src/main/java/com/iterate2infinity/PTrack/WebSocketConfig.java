package com.iterate2infinity.PTrack;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
		config.setPreservePublishOrder(true);		
	}
	
	
	@Override
	public void registerStompEndpoints (StompEndpointRegistry registry) {
		//TODO: fix CORS policy (current is for development only)
		registry.addEndpoint("/ws").addInterceptors(getInterceptor()).setAllowedOrigins("http://localhost:3000").withSockJS();
		
	}
	
	private static final Logger handshakeInterceptorLogger = LoggerFactory.getLogger(HandshakeInterceptor.class);

	public HandshakeInterceptor getInterceptor() {
		return new HandshakeInterceptor() {

			@Override
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
					Map<String, Object> attributes) throws Exception {
				
				handshakeInterceptorLogger.info("In HandshakeInterceptor beforeHandshake()...");

				attributes.put("remoteAddress", request.getRemoteAddress());
				return true;
				
			}

			@Override
			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
					Exception exception) {
				
				handshakeInterceptorLogger.info("In HandshakeInterceptor afterHandshake()...");			
			}

		};
	}
	
	


}


