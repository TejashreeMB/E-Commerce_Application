package com.retail.exception;

public class OTPExpiredException extends RuntimeException {
	private String message;

	public OTPExpiredException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
