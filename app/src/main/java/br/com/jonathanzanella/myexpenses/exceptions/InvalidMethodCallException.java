package br.com.jonathanzanella.myexpenses.exceptions;

/**
 * Created by jzanella on 8/27/16.
 */

public class InvalidMethodCallException extends RuntimeException {
	public InvalidMethodCallException(String method, String klass) {
		super(method + " should not be called from " + klass);
	}
}
