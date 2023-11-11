package com.iterate2infinity.PTrack.webSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterate2infinity.PTrack.security.JWT.JwtUtils;
import com.iterate2infinity.PTrack.security.services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class ChannelInterceptorImpl implements WebSocketMessageBrokerConfigurer {
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	private static final Logger channelInterceptorLogger = LoggerFactory.getLogger(ChannelInterceptor.class);

	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel){
				
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				channelInterceptorLogger.info("In ChannelInterceptor Inboud Channel Configurer preSend() ConnectCommand=false");

				if(StompCommand.CONNECT.equals(accessor.getCommand())) {
					channelInterceptorLogger.info("In ChannelInterceptor Inboud Channel Configurer preSend() ConnectCommand=true");

					String jwt = parseJwt(accessor);

					if(jwt.equals(null) || !jwtUtils.validateJwtToken(jwt)) {
						
						channelInterceptorLogger.error("Error: JWT is null, unable to authenticate websocket connection.");
						throw new BadCredentialsException("Error: Unable to authenticate websocket connection.");
						
					} else {
						
						String email = jwtUtils.getEmailFromJwtToken(jwt);
						UserDetails userDetails = userDetailsService.loadUserByUsername(email);
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetails((String) accessor.getHeader("remoteAddress"), accessor.getSessionId()));
						
						SecurityContextHolder.getContext().setAuthentication(authentication);
						accessor.setUser(authentication);
						
						channelInterceptorLogger.info("User authenticated successfully, User: "+ authentication.getName());
					}
				}
				
				return message;
			}
		});
	}
	
	
	private String parseJwt(StompHeaderAccessor accessor) {
		String auth = accessor.getNativeHeader("Authorization").get(0);

		channelInterceptorLogger.info("In parseJwt()");
		
		if(StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
			channelInterceptorLogger.info("JWT parsed succesfully.");
			return auth.substring(7, auth.length());
		}
		channelInterceptorLogger.info("Unable to parse JWT");
		return null;
	}
}
