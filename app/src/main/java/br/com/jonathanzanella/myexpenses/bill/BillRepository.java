package br.com.jonathanzanella.myexpenses.bill;

import android.support.annotation.WorkerThread;
import android.support.test.espresso.idling.CountingIdlingResource;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.ModelRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class BillRepository  implements ModelRepository<Bill> {
	private Repository<Bill> repository;
	private BillTable billTable = new BillTable();
	private ExpenseRepository expenseRepository;

	public BillRepository(Repository<Bill> repository, ExpenseRepository expenseRepository) {
		this.repository = repository;
		this.expenseRepository = expenseRepository;
	}

	@WorkerThread
	public Bill find(final String uuid) {
		return repository.find(billTable, uuid);
	}

	@WorkerThread
	List<Bill> userBills() {
		return repository.userData(billTable);
	}

	@WorkerThread
	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(billTable);
	}

	@WorkerThread
	public List<Bill> unsync() {
		return repository.unsync(billTable);
	}

	@WorkerThread
	public List<Bill> monthly(final DateTime month) {
		final List<Expense> expenses = expenseRepository.monthly(month);
		Where query = new Where(Fields.INIT_DATE).lessThanOrEq(month.getMillis())
				.and(Fields.END_DATE).greaterThanOrEq(month.getMillis())
				.and(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID);
		List<Bill> bills = repository.query(billTable, query);
		for (int i = 0; i < bills.size(); i++) {
			Bill bill = bills.get(i);
			boolean billAlreadyPaid = false;
			for (Expense expense : expenses) {
				Bill b = repository.find(billTable, expense.getBillUuid());
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
			bill.setMonth(month);

		return bills;
	}

	@WorkerThread
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
		if(result.isValid()) {
			if(bill.getId() == 0 && bill.getUuid() == null)
				bill.setUuid(UUID.randomUUID().toString());
			if(bill.getId() == 0 && bill.getUserUuid() == null)
				bill.setUserUuid(Environment.CURRENT_USER_UUID);
			bill.setSync(false);
			repository.saveAtDatabase(billTable, bill);
		}
		return result;
	}

	@WorkerThread
	@Override
	public void syncAndSave(final Bill unsyncBill) {
		final CountingIdlingResource idlingResource = new CountingIdlingResource("BillRepositorySave");
		idlingResource.increment();
		Bill bill = find(unsyncBill.getUuid());
		if(bill != null && bill.getId() != unsyncBill.getId()) {
			if(bill.getUpdatedAt() != unsyncBill.getUpdatedAt())
				warning("Bill overwritten", unsyncBill.getData());
			unsyncBill.setId(bill.getId());
		}

		unsyncBill.setSync(true);
		repository.saveAtDatabase(billTable, unsyncBill);
		idlingResource.decrement();
	}
}