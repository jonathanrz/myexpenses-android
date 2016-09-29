package br.com.jonathanzanella.myexpenses.expense;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

public class ExpenseRepository {
	private From<Expense> initQuery() {
		return SQLite.select().from(Expense.class);
	}

	public Expense find(String uuid) {
		return initQuery().where(Expense_Table.uuid.eq(uuid)).querySingle();
	}

	List<Expense> userExpenses() {
		return initQuery()
				.where(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Expense_Table.date, true)
				.queryList();
	}

	List<Expense> monthly(DateTime month) {
		return initQuery()
				.where(Expense_Table.date
						.between(DateHelper.firstDayOfMonth(month))
						.and(DateHelper.lastDayOfMonth(month)))
				.and(Expense_Table.removed.is(false))
				.and(Expense_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.queryList();
	}

	public OperationResult save(Expense receipt) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(receipt.getName()))
			result.addError(ValidationError.NAME);
		if(receipt.getValue() <= 0)
			result.addError(ValidationError.AMOUNT);
		if(receipt.getDate() == null)
			result.addError(ValidationError.DATE);
		if(receipt.getChargeable() == null)
			result.addError(ValidationError.CHARGEABLE);
		if(result.isValid())
			receipt.save();
		return result;
	}
}