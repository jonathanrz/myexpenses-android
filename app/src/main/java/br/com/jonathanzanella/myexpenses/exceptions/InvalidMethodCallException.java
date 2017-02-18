package br.com.jonathanzanella.myexpenses.exceptions;

public class InvalidMethodCallException extends RuntimeException {
	public InvalidMethodCallException(String method, String klass, String reason) {
		super(method + " should not be called from " + klass + ": " + reason);
	}
}
