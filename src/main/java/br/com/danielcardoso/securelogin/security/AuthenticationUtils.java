package br.com.danielcardoso.securelogin.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import br.com.danielcardoso.securelogin.user.domain.Role;

public final class AuthenticationUtils {

	public static final String USERS_PAGE = "/users";
	public static final String PROFILE_PAGE = "/profile";

	private AuthenticationUtils() {
	}

	public static boolean isLoggedIn(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken);
	}

	public static String homeFor(Authentication authentication) {
		boolean canSeeUsers = authentication.getAuthorities().stream()
				.anyMatch(a -> Role.MANAGER.authority().equals(a.getAuthority())
						|| Role.ADMIN.authority().equals(a.getAuthority()));
		return canSeeUsers ? USERS_PAGE : PROFILE_PAGE;
	}
}
