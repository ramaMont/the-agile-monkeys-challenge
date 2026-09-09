package com.aifindr.gateway.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class AppUser {

	@Id
	private String id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String passwordHash;

	protected AppUser() {
	}

	public static AppUser create(String username, String passwordHash) {
		AppUser user = new AppUser();
		user.id = UUID.randomUUID().toString();
		user.username = username;
		user.passwordHash = passwordHash;
		return user;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
}
