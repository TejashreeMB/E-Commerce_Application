package com.retail.exception;

public class InvalidUserCredentialsException extends RuntimeException {
	private String message;

	public InvalidUserCredentialsException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
