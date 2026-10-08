package br.com.danielcardoso.securelogin.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.danielcardoso.securelogin.config.AppProperties;
import br.com.danielcardoso.securelogin.user.domain.Role;
import br.com.danielcardoso.securelogin.user.domain.User;
import br.com.danielcardoso.securelogin.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

	@Mock
	UserRepository repository;

	User user;
	LoginAttemptService service;

	@BeforeEach
	void setUp() {
		AppProperties properties = new AppProperties(
				new AppProperties.Theme("default", "layouts/main"),
				new AppProperties.Security(5, Duration.ofMinutes(15), 3, Duration.ofMinutes(30), false),
				new AppProperties.Admin("Admin", null, null));
		service = new LoginAttemptService(repository, properties);
		user = new User("Maria", "maria@exemplo.com", "x", Role.USER);
		when(repository.findByEmail("maria@exemplo.com")).thenReturn(Optional.of(user));
	}

	@Test
	void locksAccountAfterMaxFailedAttempts() {
		for (int i = 0; i < 4; i++) {
			service.recordFailure("Maria@Exemplo.com");
		}
		assertThat(user.isLocked(Instant.now())).isFalse();

		service.recordFailure("maria@exemplo.com");

		assertThat(user.isLocked(Instant.now())).isTrue();
		assertThat(new AuthenticatedUser(user).isAccountNonLocked()).isFalse();
	}

	@Test
	void successfulLoginResetsCounter() {
		service.recordFailure("maria@exemplo.com");
		service.recordFailure("maria@exemplo.com");

		service.recordSuccess("maria@exemplo.com");

		assertThat(user.getFailedLoginAttempts()).isZero();
		assertThat(user.getLastLoginAt()).isNotNull();
	}

	@Test
	void expiredLockStartsNewCount() {
		user.setFailedLoginAttempts(5);
		user.setLockedUntil(Instant.now().minusSeconds(1));

		service.recordFailure("maria@exemplo.com");

		assertThat(user.getFailedLoginAttempts()).isEqualTo(1);
		assertThat(user.isLocked(Instant.now())).isFalse();
	}
}
