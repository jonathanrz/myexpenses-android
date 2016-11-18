package br.com.jonathanzanella.myexpenses.bill;

import android.support.test.espresso.idling.CountingIdlingResource;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import rx.Observable;
import rx.schedulers.Schedulers;

import static br.com.jonathanzanella.myexpenses.log.Log.warning;

/**
 * Created by jzanella on 8/27/16.
 */

public class BillRepository {
	private Repository<Bill> repository;
	private BillTable billTable = new BillTable();

	public BillRepository(Repository<Bill> repository) {
		this.repository = repository;
	}

	public Observable<Bill> find(String uuid) {
		return repository.find(billTable, uuid);
	}

	List<Bill> userBills() {
		return repository.userData(billTable);
	}

	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(billTable);
	}

	public List<Bill> unsync() {
		return repository.unsync(billTable);
	}

	public List<Bill> monthly(DateTime month) {
//		List<Expense> expenses = Expense.monthly(month);
		Where query = new Where(Fields.INIT_DATE).lessThanOrEq(month.getMillis())
				.and(Fields.END_DATE).greaterThanOrEq(month.getMillis())
				.and(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID);
		List<Bill> bills = repository.query(billTable, query);

//		for (int i = 0; i < bills.size(); i++) {
//			Bill bill = bills.get(i);
//			boolean billAlreadyPaid = false;
//			for (Expense expense : expenses) {
//				Bill b = expense.getBill();
//				if(b != null && b.getUuid().equals(bill.getUuid())) {
//					billAlreadyPaid = true;
//					break;
//				}
//			}
//			if(billAlreadyPaid) {
//				bills.remove(i);
//				i--;
//			}
//		}

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

	public void syncAndSave(final Bill unsyncBill) {
		final CountingIdlingResource idlingResource = new CountingIdlingResource("BillRepositorySave");
		idlingResource.increment();
		find(unsyncBill.getUuid())
				.observeOn(Schedulers.io())
				.subscribe(new Subscriber<Bill>("BillRepository.") {
					@Override
					public void onNext(Bill bill) {
						if(bill != null && bill.id != unsyncBill.getId()) {
							if(bill.getUpdatedAt() != unsyncBill.getUpdatedAt())
								warning("Bill overwritten", unsyncBill.getData());
							unsyncBill.setId(bill.id);
						}

						unsyncBill.setSync(true);
						repository.saveAtDatabase(billTable, unsyncBill);
						idlingResource.decrement();
					}
				});
	}
}