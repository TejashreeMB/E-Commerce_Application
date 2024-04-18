package com.retail.exception;

public class InvalidUserRoleException extends RuntimeException{
	private String message;

	public InvalidUserRoleException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
