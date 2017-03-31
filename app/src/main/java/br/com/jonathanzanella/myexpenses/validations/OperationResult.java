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

	public String getErrorsAsString() {
		StringBuilder builder = new StringBuilder();
		for (ValidationError error : errors) {
			if(builder.length() != 0)
				builder.append("\n");
			builder.append(error.toString());
		}
		return builder.toString();
	}
}
