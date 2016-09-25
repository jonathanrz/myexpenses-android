package br.com.jonathanzanella.myexpenses.validations;

import br.com.jonathanzanella.myexpenses.R;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by jzanella on 8/27/16.
 */

@ToString
public enum ValidationError {
	NAME(R.string.error_message_name_not_informed),
	CARD_TYPE(R.string.error_message_card_type_not_selected),
	ACCOUNT(R.string.error_message_account_not_informed),
	SOURCE(R.string.error_message_source_not_informed),
	AMOUNT(R.string.error_message_amount_zero),
	DUE_DATE(R.string.error_message_due_date_not_informed),
	INIT_DATE(R.string.error_message_init_date_not_informed),
	END_DATE(R.string.error_message_end_date_not_informed),
	DATE(R.string.error_message_date_not_informed),
	INIT_DATE_GREATER_THAN_END_DATE(R.string.error_message_init_date_greater_than_end_date);

	@Getter
	private int message;

	ValidationError(int message) {
		this.message = message;
	}
}