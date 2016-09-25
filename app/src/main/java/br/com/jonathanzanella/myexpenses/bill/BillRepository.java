package br.com.jonathanzanella.myexpenses.bill;

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

public class BillRepository {
	private From<Bill> initQuery() {
		return SQLite.select().from(Bill.class);
	}

	public Bill find(String uuid) {
		return initQuery().where(Bill_Table.uuid.eq(uuid)).querySingle();
	}

	List<Bill> userBills() {
		return initQuery().where(Bill_Table.userUuid.is(Environment.CURRENT_USER_UUID)).queryList();
	}

	public OperationResult save(Bill bill) {
		OperationResult result = new OperationResult();
		if(StringUtils.isEmpty(bill.getName()))
			result.addError(ValidationError.NAME);
		if(bill.getAmount() <= 0)
			result.addError(ValidationError.AMOUNT);
		if(bill.getDueDate() <= 0)
			result.addError(ValidationError.DUE_DATE);
		if(bill.getInitDate() == null)
			result.addError(ValidationError.INIT_DATE);
		if(bill.getEndDate() == null)
			result.addError(ValidationError.END_DATE);
		if(bill.getInitDate() != null && bill.getEndDate() != null && bill.getInitDate().isAfter(bill.getEndDate()))
			result.addError(ValidationError.INIT_DATE_GREATER_THAN_END_DATE);
		if(result.isValid())
			bill.save();
		return result;
	}
}