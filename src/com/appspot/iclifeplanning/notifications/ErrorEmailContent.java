package com.appspot.iclifeplanning.notifications;

public class ErrorEmailContent implements EmailContent {

	public static final String TOKEN_PROBLEM 
	    = "Hi! You need to reset the right for our application!";
	private String errorMessage;
	
	public ErrorEmailContent(String message) {
		errorMessage = message;
	}

	public String toString() {
		return errorMessage;
	}
}
