package com.iterate2infinity.PTrack;

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
	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel){
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if(StompCommand.CONNECT.equals(accessor.getCommand())) {
					String jwt = parseJwt(accessor);
					
					if(jwt!=null && jwtUtils.validateJwtToken(jwt)) {
						String email = jwtUtils.getEmailFromJwtToken(jwt);
						UserDetails userDetails = userDetailsService.loadUserByUsername(email);
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetails((String) accessor.getHeader("remoteAddress"), accessor.getSessionId()));
						
						SecurityContextHolder.getContext().setAuthentication(authentication);
						accessor.setUser(authentication);
					}
					
					
				}
				
				return message;
			}
		});
	}
	
	
	private String parseJwt(StompHeaderAccessor accessor) {
		String headerAuth = (String) accessor.getHeader("Authorization");
		
		if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}
		
		return null;
	}
}
