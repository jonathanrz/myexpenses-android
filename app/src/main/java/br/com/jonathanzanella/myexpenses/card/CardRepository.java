package br.com.jonathanzanella.myexpenses.card;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

class CardRepository {
	private From<Card> initQuery() {
		return SQLite.select().from(Card.class);
	}

	Card find(String uuid) {
		return initQuery().where(Card_Table.uuid.eq(uuid)).querySingle();
	}

	List<Card> userCards() {
		return initQuery().where(Card_Table.userUuid.is(Environment.CURRENT_USER_UUID)).queryList();
	}

	OperationResult save(Card card) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(card.getName()))
			result.addError(ValidationError.NAME);
		if(card.getType() == null)
			result.addError(ValidationError.CARD_TYPE);
		if(card.getAccount() == null)
			result.addError(ValidationError.ACCOUNT);
		if(result.isValid())
			card.save();
		return result;
	}
}