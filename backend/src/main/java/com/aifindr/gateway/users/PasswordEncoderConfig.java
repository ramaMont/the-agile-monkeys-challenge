package com.aifindr.gateway.users;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		// Delegating keeps {noop} OAuth client secrets working alongside {bcrypt} user hashes.
		DelegatingPasswordEncoder encoder =
				(DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
		encoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
		return encoder;
	}
}
