package br.com.jonathanzanella.myexpenses.receipt;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.database.Fields;
import br.com.jonathanzanella.myexpenses.database.ModelRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.database.Where;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static br.com.jonathanzanella.myexpenses.helpers.DateHelper.firstDayOfMonth;
import static br.com.jonathanzanella.myexpenses.helpers.DateHelper.lastDayOfMonth;
import static br.com.jonathanzanella.myexpenses.log.Log.warning;

public class ReceiptRepository implements ModelRepository<Receipt> {
	private final Repository<Receipt> repository;
	private final ReceiptTable table = new ReceiptTable();

	public ReceiptRepository(Repository<Receipt> repository) {
		this.repository = repository;
	}

	@WorkerThread
	public Receipt find(String uuid) {
		return repository.find(table, uuid);
	}

	@WorkerThread
	List<Receipt> all() {
		return repository.query(table, new Where(null).orderBy(Fields.DATE));
	}

	@WorkerThread
	public List<Receipt> monthly(DateTime month) {
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
				.orderBy(Fields.DATE);
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
	public ValidationResult save(Receipt receipt) {
		ValidationResult result = validate(receipt);
		if(result.isValid()) {
			if(receipt.getId() == 0 && receipt.getUuid() == null)
				receipt.setUuid(UUID.randomUUID().toString());
			receipt.setSync(false);
			repository.saveAtDatabase(table, receipt);
		}
		return result;
	}

	@NonNull
	private ValidationResult validate(Receipt receipt) {
		ValidationResult result = new ValidationResult();
		if(StringUtils.isEmpty(receipt.getName()))
			result.addError(ValidationError.NAME);
		if(receipt.getAmount() <= 0)
			result.addError(ValidationError.AMOUNT);
		if(receipt.getSource() == null)
			result.addError(ValidationError.SOURCE);
		if(receipt.getAccountFromCache() == null)
			result.addError(ValidationError.ACCOUNT);
		if(receipt.getDate() == null)
			result.addError(ValidationError.DATE);
		return result;
	}

	@WorkerThread
	@Override
	public ValidationResult syncAndSave(final Receipt unsyncReceipt) {
		ValidationResult result = validate(unsyncReceipt);
		if(!result.isValid()) {
			warning("Receipt sync validation failed", unsyncReceipt.getData() + "\nerrors: " + result.getErrorsAsString());
			return result;
		}

		Receipt receipt = find(unsyncReceipt.getUuid());
		if(receipt != null && receipt.getId() != unsyncReceipt.getId()) {
			if(receipt.getUpdatedAt() != unsyncReceipt.getUpdatedAt())
				warning("Receipt overwritten", unsyncReceipt.getData());
			unsyncReceipt.setId(receipt.getId());
		}

		unsyncReceipt.setSync(true);
		repository.saveAtDatabase(table, unsyncReceipt);

		return result;
	}
}