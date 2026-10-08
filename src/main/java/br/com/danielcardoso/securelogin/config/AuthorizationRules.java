package br.com.danielcardoso.securelogin.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

import br.com.danielcardoso.securelogin.user.domain.Role;

public final class AuthorizationRules {

	public static final String[] PUBLIC_PATHS = {
			"/", "/login", "/register", "/error", "/error/**",
			"/css/**", "/js/**", "/images/**", "/themes/**", "/favicon.ico"
	};

	private AuthorizationRules() {
	}

	public static void apply(
			AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
		auth
				.requestMatchers(PUBLIC_PATHS).permitAll()
				.requestMatchers(HttpMethod.POST, "/users/**").hasRole(Role.ADMIN.name())
				.requestMatchers("/users/**").hasRole(Role.MANAGER.name())
				.anyRequest().authenticated();
	}
}
