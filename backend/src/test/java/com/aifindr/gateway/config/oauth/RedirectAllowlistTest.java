package com.aifindr.gateway.config.oauth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedirectAllowlistTest {

	@Test
	void allowsAifindrHttpsCallbacks() {
		assertTrue(RedirectAllowlist.isAllowed("https://api-dev.saas.aifindr.ai/oauth/callback"));
		assertTrue(RedirectAllowlist.isAllowed("https://hub-dev.aifindr.ai/admin/oauth/callback"));
	}

	@Test
	void rejectsOtherHostsAndHttpAifindr() {
		assertFalse(RedirectAllowlist.isAllowed("http://api-dev.saas.aifindr.ai/oauth/callback"));
		assertFalse(RedirectAllowlist.isAllowed("https://evil.example/oauth/callback"));
		assertFalse(RedirectAllowlist.isAllowed("https://aifindr.ai.evil.com/oauth/callback"));
	}
}
