package br.com.danielcardoso.securelogin.common.validation;

public interface PasswordConfirmation {

	String passwordToConfirm();

	String confirmation();

	default String confirmationField() {
		return "confirmPassword";
	}
}
