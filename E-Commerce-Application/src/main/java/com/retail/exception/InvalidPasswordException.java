package com.retail.exception;

public class InvalidPasswordException extends RuntimeException{
	private String message;

	public String getMessage() {
		return message;
	}

	public InvalidPasswordException(String message) {
		super();
		this.message = message;
	}
}
