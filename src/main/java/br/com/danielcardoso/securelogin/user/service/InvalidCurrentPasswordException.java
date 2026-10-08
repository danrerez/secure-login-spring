package br.com.danielcardoso.securelogin.user.service;

import br.com.danielcardoso.securelogin.common.exception.BusinessException;

public class InvalidCurrentPasswordException extends BusinessException {

	public InvalidCurrentPasswordException() {
		super("A senha atual está incorreta");
	}
}
