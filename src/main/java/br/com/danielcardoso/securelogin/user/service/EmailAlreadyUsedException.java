package br.com.danielcardoso.securelogin.user.service;

import br.com.danielcardoso.securelogin.common.exception.BusinessException;

public class EmailAlreadyUsedException extends BusinessException {

	public EmailAlreadyUsedException() {
		super("Este e-mail já está cadastrado");
	}
}
