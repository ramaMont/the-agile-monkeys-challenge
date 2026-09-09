package com.aifindr.gateway.config.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Set;

import static org.springaicommunity.mcp.security.authorizationserver.config.McpAuthorizationServerConfigurer.mcpAuthorizationServer;
import static org.springaicommunity.mcp.security.server.config.McpServerOAuth2Configurer.mcpServerOAuth2;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.with(mcpAuthorizationServer(), mcp -> {
					mcp.dynamicClientRegistration(false);
					mcp.cimd(false);
					mcp.authorizationCodeRequestValidator(new AifindrAuthorizeValidator());
					mcp.authorizationServer(authzServer -> {
						authzServer.authorizationServerMetadataEndpoint(endpoint ->
								endpoint.authorizationServerMetadataCustomizer(builder ->
										builder.claim(
												"token_endpoint_auth_methods_supported",
												List.of("client_secret_basic", "client_secret_post"))));
						http.securityMatcher(new OrRequestMatcher(
								authzServer.getEndpointsMatcher(),
								PathPatternRequestMatcher.withDefaults().matcher("/.well-known/openid-configuration")));
					});
				})
				.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
						new LoginUrlAuthenticationEntryPoint("/login"), htmlRequestMatcher()))
				.build();
	}

	@Bean
	@Order(1)
	SecurityFilterChain mcpSecurityFilterChain(
			HttpSecurity http,
			OAuthIssuerProperties oauth,
			JwtDecoder jwtDecoder) throws Exception {
		return http.securityMatcher(
						"/mcp",
						"/mcp/**",
						"/.well-known/oauth-protected-resource",
						"/.well-known/oauth-protected-resource/**")
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/.well-known/**").permitAll()
						.anyRequest().authenticated())
				.csrf(AbstractHttpConfigurer::disable)
				.headers(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.with(mcpServerOAuth2(), mcp -> {
					mcp.authorizationServer(oauth.canonicalIssuerUrl());
					mcp.resourceName("secure-actions-gateway");
					mcp.jwtDecoder(jwtDecoder);
				})
				.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
		return http.securityMatcher("/v1/**")
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/v1/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
						.anyRequest().authenticated())
				.cors(withDefaults())
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))
				.build();
	}

	@Bean
	@Order(3)
	SecurityFilterChain appSecurityFilterChain(HttpSecurity http, UserDetailsService users) throws Exception {
		return http.authorizeHttpRequests(auth -> auth
						.requestMatchers("/health", "/error").permitAll()
						.anyRequest().authenticated())
				.csrf(csrf -> csrf.ignoringRequestMatchers("/health", "/error"))
				.userDetailsService(users)
				.formLogin(withDefaults())
				.build();
	}

	private static RequestMatcher htmlRequestMatcher() {
		MediaTypeRequestMatcher matcher = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
		matcher.setIgnoredMediaTypes(Set.of(MediaType.ALL));
		return matcher;
	}
}
