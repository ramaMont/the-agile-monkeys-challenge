package com.aifindr.gateway.config.oauth;

import com.nimbusds.jose.jwk.RSAKey;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.UUID;

final class RsaKeyLoader {

	private RsaKeyLoader() {
	}

	static RSAKey loadOrGenerate(String pem, String keyId) {
		if (pem == null || pem.isBlank()) {
			return generate(UUID.randomUUID().toString());
		}
		String kid = (keyId == null || keyId.isBlank()) ? "gateway-1" : keyId;
		return fromPem(pem, kid);
	}

	static RSAKey fromPem(String pem, String keyId) {
		try {
			byte[] der = decodePkcs8Pem(normalizePem(pem));
			KeyFactory factory = KeyFactory.getInstance("RSA");
			RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) factory.generatePrivate(new PKCS8EncodedKeySpec(der));
			RSAPublicKey publicKey = (RSAPublicKey) factory.generatePublic(
					new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));
			return new RSAKey.Builder(publicKey)
					.privateKey(privateKey)
					.keyID(keyId)
					.build();
		}
		catch (IllegalArgumentException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new IllegalStateException(
					"Could not load OAUTH_RSA_PRIVATE_KEY (expect PKCS#8 PEM: BEGIN PRIVATE KEY)", ex);
		}
	}

	private static RSAKey generate(String keyId) {
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
			KeyPair keyPair = generator.generateKeyPair();
			return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
					.privateKey((RSAPrivateKey) keyPair.getPrivate())
					.keyID(keyId)
					.build();
		}
		catch (Exception ex) {
			throw new IllegalStateException("Could not generate RSA key for OAuth", ex);
		}
	}

	private static String normalizePem(String pem) {
		return pem.strip().replace("\\n", "\n");
	}

	private static byte[] decodePkcs8Pem(String pem) {
		String normalized = pem.replace("\r\n", "\n");
		if (normalized.contains("BEGIN RSA PRIVATE KEY")) {
			throw new IllegalArgumentException(
					"PKCS#1 PEM not supported; convert with: openssl pkcs8 -topk8 -nocrypt -in key.pem -out pkcs8.pem");
		}
		if (!normalized.contains("BEGIN PRIVATE KEY")) {
			throw new IllegalArgumentException("Missing BEGIN PRIVATE KEY header in OAUTH_RSA_PRIVATE_KEY");
		}
		String base64 = normalized
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");
		return Base64.getDecoder().decode(base64);
	}
}
