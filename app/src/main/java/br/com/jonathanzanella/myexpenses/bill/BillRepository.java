package br.com.jonathanzanella.myexpenses.bill;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.expense.Expense;
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
		return initQuery()
				.where(Bill_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.orderBy(Source_Table.name, true)
				.queryList();
	}

	public long greaterUpdatedAt() {
		Bill bill = initQuery().orderBy(Bill_Table.updatedAt, false).limit(1).querySingle();
		if(bill == null)
			return 0L;
		return bill.getUpdatedAt();
	}

	public List<Bill> unsync() {
		return initQuery().where(Bill_Table.sync.eq(false)).queryList();
	}

	public List<Bill> monthly(DateTime month) {
		List<Expense> expenses = Expense.monthly(month);
		List<Bill> bills = initQuery()
				.where(Bill_Table.initDate.lessThanOrEq(month))
				.and(Bill_Table.endDate.greaterThanOrEq(month))
				.and(Bill_Table.userUuid.is(Environment.CURRENT_USER_UUID))
				.queryList();

		for (int i = 0; i < bills.size(); i++) {
			Bill bill = bills.get(i);
			boolean billAlreadyPaid = false;
			for (Expense expense : expenses) {
				Bill b = expense.getBill();
				if(b != null && b.getUuid().equals(bill.getUuid())) {
					billAlreadyPaid = true;
					break;
				}
			}
			if(billAlreadyPaid) {
				bills.remove(i);
				i--;
			}
		}

		for (Bill bill : bills)
			bill.month = month;

		return bills;
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