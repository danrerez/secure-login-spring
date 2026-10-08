package br.com.danielcardoso.securelogin.config;

import java.util.Map;

import org.mongodb.spring.session.MongoSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import br.com.danielcardoso.securelogin.security.LoginFailureHandler;
import br.com.danielcardoso.securelogin.security.LoginSuccessHandler;
import br.com.danielcardoso.securelogin.user.domain.Role;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	static final int BCRYPT_STRENGTH = 12;

	static final String CONTENT_SECURITY_POLICY = "default-src 'self'; script-src 'self'; style-src 'self'; "
			+ "img-src 'self' data:; font-src 'self'; object-src 'none'; base-uri 'self'; "
			+ "form-action 'self'; frame-ancestors 'none'";

	@Bean
	PasswordEncoder passwordEncoder() {
		return new DelegatingPasswordEncoder("bcrypt", Map.of("bcrypt", new BCryptPasswordEncoder(BCRYPT_STRENGTH)));
	}

	@Bean
	static RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl.withDefaultRolePrefix()
				.role(Role.ADMIN.name()).implies(Role.MANAGER.name())
				.role(Role.MANAGER.name()).implies(Role.USER.name())
				.build();
	}

	@Bean
	SpringSessionBackedSessionRegistry<MongoSession> sessionRegistry(
			FindByIndexNameSessionRepository<MongoSession> sessionRepository) {
		return new SpringSessionBackedSessionRegistry<>(sessionRepository);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, LoginSuccessHandler successHandler,
			LoginFailureHandler failureHandler, SpringSessionBackedSessionRegistry<MongoSession> sessionRegistry,
			AppProperties properties) throws Exception {
		http
				.authorizeHttpRequests(AuthorizationRules::apply)
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/login")
						.usernameParameter(LoginFailureHandler.USERNAME_PARAMETER)
						.passwordParameter("password")
						.successHandler(successHandler)
						.failureHandler(failureHandler)
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login?logout")
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.deleteCookies(MongoSessionConfig.COOKIE_NAME)
						.permitAll())
				.sessionManagement(session -> session
						.sessionFixation(fixation -> fixation.changeSessionId())
						.sessionConcurrency(concurrency -> concurrency
								.maximumSessions(properties.security().maxSessionsPerUser())
								.sessionRegistry(sessionRegistry)
								.expiredUrl("/login?expired")))
				.headers(headers -> headers
						.contentSecurityPolicy(csp -> csp.policyDirectives(CONTENT_SECURITY_POLICY))
						.frameOptions(frame -> frame.deny())
						.referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)));
		return http.build();
	}
}
