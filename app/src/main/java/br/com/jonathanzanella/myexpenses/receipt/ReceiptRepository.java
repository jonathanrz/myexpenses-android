package br.com.jonathanzanella.myexpenses.receipt;

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

class ReceiptRepository {
	private From<Receipt> initQuery() {
		return SQLite.select().from(Receipt.class);
	}

	public Receipt find(String uuid) {
		return initQuery().where(Receipt_Table.uuid.eq(uuid)).querySingle();
	}

	List<Receipt> userReceipts() {
		return initQuery()
				.where(Receipt_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Receipt_Table.date, true)
				.queryList();
	}

	public OperationResult save(Receipt receipt) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(receipt.getName()))
			result.addError(ValidationError.NAME);
		if(receipt.getAmount() <= 0)
			result.addError(ValidationError.AMOUNT);
		if(receipt.getSource() == null)
			result.addError(ValidationError.SOURCE);
		if(receipt.getAccount() == null)
			result.addError(ValidationError.ACCOUNT);
		if(receipt.getDate() == null)
			result.addError(ValidationError.DATE);
		if(result.isValid())
			receipt.save();
		return result;
	}
}