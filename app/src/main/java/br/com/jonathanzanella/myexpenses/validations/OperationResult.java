package br.com.jonathanzanella.myexpenses.validations;

import java.util.ArrayList;
import java.util.List;

public class OperationResult {
	private final List<ValidationError> errors = new ArrayList<>();

	public boolean isValid() {
		return errors.isEmpty();
	}

	public void addError(ValidationError error) {
		errors.add(error);
	}

	public List<ValidationError> getErrors() {
		return errors;
	}
}
