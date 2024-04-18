package com.retail.exception;

public class InvalidUserEmailException extends RuntimeException {
	private String message;

	public InvalidUserEmailException(String message) {
		super();
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
