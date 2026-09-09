package com.aifindr.gateway.users;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserSeeder implements ApplicationRunner {

	private final AppUserRepository users;
	private final PasswordEncoder passwordEncoder;
	private final String username;
	private final String rawPassword;

	public DefaultUserSeeder(
			AppUserRepository users,
			PasswordEncoder passwordEncoder,
			@Value("${app.admin.username:user}") String username,
			@Value("${app.admin.password:password}") String rawPassword) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.username = username;
		this.rawPassword = stripDelegatingPrefix(rawPassword);
	}

	@Override
	public void run(ApplicationArguments args) {
		if (users.existsByUsername(username)) {
			return;
		}
		users.save(AppUser.create(username, passwordEncoder.encode(rawPassword)));
	}

	private static String stripDelegatingPrefix(String password) {
		if (password.startsWith("{") && password.contains("}")) {
			return password.substring(password.indexOf('}') + 1);
		}
		return password;
	}
}
