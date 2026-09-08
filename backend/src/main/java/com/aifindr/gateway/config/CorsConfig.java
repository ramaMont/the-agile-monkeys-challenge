package com.aifindr.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig implements WebMvcConfigurer {

	private final CorsProperties cors;

	public CorsConfig(CorsProperties cors) {
		this.cors = cors;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/v1/**")
				.allowedOrigins(cors.allowedOrigin())
				.allowedMethods("GET");
	}
}
