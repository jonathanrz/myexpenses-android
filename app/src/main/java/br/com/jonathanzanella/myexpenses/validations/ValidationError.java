package br.com.jonathanzanella.myexpenses.validations;

import br.com.jonathanzanella.myexpenses.R;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by jzanella on 8/27/16.
 */

@ToString
public enum ValidationError {
	NAME(R.string.error_message_name_not_informed);

	@Getter
	private int message;

	ValidationError(int message) {
		this.message = message;
	}
}