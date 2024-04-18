package com.retail.exception;

public class RegistraionSessionExpiredException extends RuntimeException {
	private String message;

	public RegistraionSessionExpiredException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
