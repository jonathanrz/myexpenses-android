package br.com.jonathanzanella.myexpenses.receipt;

import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static br.com.jonathanzanella.myexpenses.helpers.DateHelper.firstDayOfMonth;
import static br.com.jonathanzanella.myexpenses.helpers.DateHelper.lastDayOfMonth;
import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class ReceiptRepository {
	private Repository<Receipt> repository;
	private ReceiptTable table = new ReceiptTable();

	public ReceiptRepository(Repository<Receipt> repository) {
		this.repository = repository;
	}

	@WorkerThread
	public Receipt find(String uuid) {
		return repository.find(table, uuid);
	}

	@WorkerThread
	List<Receipt> userReceipts() {
		return repository.userData(table);
	}

	@WorkerThread
	List<Receipt> monthly(DateTime month) {
		return repository.query(table, monthlyQuery(month, null));
	}

	@WorkerThread
	public List<Receipt> monthly(DateTime month, Account account) {
		return repository.query(table, monthlyQuery(month, account));
	}

	@WorkerThread
	private Where monthlyQuery(DateTime month, Account account) {
		Where where = new Where(Fields.DATE).greaterThanOrEq(firstDayOfMonth(month).getMillis())
				.and(Fields.DATE).lessThanOrEq(lastDayOfMonth(month).getMillis())
				.and(Fields.REMOVED).eq(false)
				.and(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID);
		if(account != null)
			where = where.and(Fields.ACCOUNT_UUID).eq(account.getUuid());

		return where;
	}

	@WorkerThread
	public List<Receipt> resume(DateTime month) {
		return repository.query(table, new Where(Fields.DATE).greaterThanOrEq(month.getMillis())
							.and(Fields.DATE).lessThanOrEq(month.plusMonths(1).getMillis())
							.and(Fields.IGNORE_IN_RESUME).eq(false)
							.and(Fields.REMOVED).eq(false)
							.and(Fields.USER_UUID).eq(Environment.CURRENT_USER_UUID)
							.orderBy(Fields.DATE));
	}

	@WorkerThread
	public long greaterUpdatedAt() {
		return repository.greaterUpdatedAt(table);
	}

	@WorkerThread
	public List<Receipt> unsync() {
		return repository.unsync(table);
	}

	@WorkerThread
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
		if(result.isValid()) {
			if(receipt.getId() == 0 && receipt.getUuid() == null)
				receipt.setUuid(UUID.randomUUID().toString());
			if(receipt.getId() == 0 && receipt.getUserUuid() == null)
				receipt.setUserUuid(Environment.CURRENT_USER_UUID);
			receipt.setSync(false);
			repository.saveAtDatabase(table, receipt);
		}
		return result;
	}

	@UiThread
	void saveAsync(final Receipt receipt) {
		new AsyncTask<Void, Void, OperationResult>() {

			@Override
			protected OperationResult doInBackground(Void... voids) {
				return save(receipt);
			}

			@Override
			protected void onPostExecute(OperationResult operationResult) {
				super.onPostExecute(operationResult);
				if(!operationResult.isValid())
					throw new UnsupportedOperationException("Could not save receipt " + receipt.getUuid());
			}
		}.execute();
	}

	@WorkerThread
	public void syncAndSave(final Receipt unsyncReceipt) {
		Receipt receipt = find(unsyncReceipt.getUuid());
		if(receipt != null && receipt.id != unsyncReceipt.getId()) {
			if(receipt.getUpdatedAt() != unsyncReceipt.getUpdatedAt())
				warning("Receipt overwritten", unsyncReceipt.getData());
			unsyncReceipt.setId(receipt.id);
		}

		unsyncReceipt.setSync(true);
		repository.saveAtDatabase(table, unsyncReceipt);
	}
}