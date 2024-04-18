package com.retail.exception;

public class InvalidOTPException extends RuntimeException{
	private String message;

	public InvalidOTPException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
