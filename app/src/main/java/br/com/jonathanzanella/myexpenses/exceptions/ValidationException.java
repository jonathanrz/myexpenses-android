package br.com.jonathanzanella.myexpenses.exceptions;

import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

public class ValidationException extends RuntimeException {

	public ValidationException(ValidationResult validationResult) {
		super(validationResult.getErrorsAsString());
	}
}
