package com.aifindr.gateway.config.oauth;

import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RsaKeyLoaderTest {

	@Test
	void loadsSameKeyIdFromPemAcrossCalls() throws Exception {
		String pem = toPkcs8Pem(generatePrivateKey());

		RSAKey first = RsaKeyLoader.fromPem(pem, "gateway-1");
		RSAKey second = RsaKeyLoader.fromPem(pem.replace("\n", "\\n"), "gateway-1");

		assertThat(first.getKeyID()).isEqualTo("gateway-1");
		assertThat(second.getKeyID()).isEqualTo("gateway-1");
		assertThat(first.toRSAPublicKey().getModulus()).isEqualTo(second.toRSAPublicKey().getModulus());
	}

	@Test
	void blankPemGeneratesEphemeralKey() throws Exception {
		RSAKey key = RsaKeyLoader.loadOrGenerate("  ", "gateway-1");
		assertThat(key.getKeyID()).isNotBlank();
		assertThat(key.toRSAPrivateKey()).isNotNull();
	}

	@Test
	void rejectsPkcs1Pem() {
		assertThatThrownBy(() -> RsaKeyLoader.fromPem(
				"-----BEGIN RSA PRIVATE KEY-----\nQQ==\n-----END RSA PRIVATE KEY-----",
				"gateway-1"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("PKCS#1");
	}

	private static RSAPrivateKey generatePrivateKey() throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		KeyPair pair = generator.generateKeyPair();
		return (RSAPrivateKey) pair.getPrivate();
	}

	private static String toPkcs8Pem(RSAPrivateKey privateKey) {
		String body = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(privateKey.getEncoded());
		return "-----BEGIN PRIVATE KEY-----\n" + body + "\n-----END PRIVATE KEY-----";
	}
}
