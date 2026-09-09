package com.aifindr.gateway.config.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class LoginUserConfig {

	@Bean
	UserDetailsService userDetailsService(
			@Value("${spring.security.user.name:user}") String username,
			@Value("${spring.security.user.password:password}") String password) {
		UserDetails user = User.withUsername(username)
				.password(withDelegatingPrefix(password))
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}

	private static String withDelegatingPrefix(String password) {
		if (password.startsWith("{")) {
			return password;
		}
		return "{noop}" + password;
	}
}
