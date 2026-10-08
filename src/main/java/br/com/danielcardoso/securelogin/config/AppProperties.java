package br.com.danielcardoso.securelogin.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app")
public record AppProperties(
		@DefaultValue Theme theme,
		@DefaultValue Security security,
		@DefaultValue Admin admin) {

	public record Theme(
			@DefaultValue("default") String name,
			@DefaultValue("layouts/main") String layout) {
	}

	public record Security(
			@DefaultValue("5") int maxLoginAttempts,
			@DefaultValue("15m") Duration lockDuration,
			@DefaultValue("3") int maxSessionsPerUser,
			@DefaultValue("30m") Duration sessionTimeout,
			@DefaultValue("false") boolean secureCookie) {
	}

	public record Admin(
			@DefaultValue("Administrador") String name,
			String email,
			String password) {
	}
}
