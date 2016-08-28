package br.com.jonathanzanella.myexpenses.validations;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by jzanella on 8/27/16.
 */

public class OperationResult {
	@Getter
	List<ValidationError> errors = new ArrayList<>();

	public boolean isValid() {
		return errors.isEmpty();
	}

	public void addError(ValidationError error) {
		errors.add(error);
	}
}
