package br.com.jonathanzanella.myexpenses.exceptions;

import br.com.jonathanzanella.myexpenses.validations.OperationResult;

public class ValidationException extends RuntimeException {

	public ValidationException(OperationResult operationResult) {
		super(operationResult.getErrorsAsString());
	}
}
