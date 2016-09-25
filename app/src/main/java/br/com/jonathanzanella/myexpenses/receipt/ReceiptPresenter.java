package br.com.jonathanzanella.myexpenses.receipt;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

class ReceiptPresenter {
	static final String KEY_RECEIPT_UUID = "KeyReceiptUuid";
	static final String KEY_SOURCE_UUID = "KeySourceUuid";
	static final String KEY_ACCOUNT_UUID = "KeyAccountUuid";

	private ReceiptContract.View view;
	private ReceiptContract.EditView editView;
	private ReceiptRepository repository;
	private SourceRepository sourceRepository;
	private AccountRepository accountRepository;
	private Receipt receipt;
	private Source source;
	private Account account;
	private DateTime date;

	ReceiptPresenter(ReceiptRepository repository, SourceRepository sourceRepository, AccountRepository accountRepository) {
		this.repository = repository;
		this.sourceRepository = sourceRepository;
		this.accountRepository = accountRepository;
	}

	void attachView(ReceiptContract.View view) {
		this.view = view;
	}

	void attachView(ReceiptContract.EditView view) {
		this.view = view;
		this.editView = view;
	}

	void detachView() {
		this.view = null;
		this.editView = null;
	}

	void viewUpdated(boolean invalidateCache) {
		if (receipt != null) {
			if(invalidateCache)
				receipt = repository.find(receipt.getUuid());
			if(editView != null) {
				editView.setTitle(R.string.edit_receipt_title);
			} else {
				String title = view.getContext().getString(R.string.receipts);
				view.setTitle(title.concat(" ").concat(receipt.getName()));
			}
			view.showReceipt(receipt);

			Source source = receipt.getSource();
			if(source != null)
				onSourceSelected(source.getUuid());

			Account account = receipt.getAccount();
			if(account != null)
				onAccountSelected(account.getUuid());

			date = receipt.getDate();
			if(date != null)
				editView.onDateChanged(date);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_receipt_title);

			date = DateTime.now();
			if(date != null)
				editView.onDateChanged(date);
		}
	}

	void loadReceipt(String uuid) {
		receipt = repository.find(uuid);
		if(receipt == null)
			throw new ReceiptNotFoundException(uuid);
	}

	private void checkEditViewSet() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
	}

	void save() {
		checkEditViewSet();
		if(receipt == null)
			receipt = new Receipt();
		receipt = editView.fillReceipt(receipt);
		if(source != null)
			receipt.setSource(source);
		if(account != null)
			receipt.setAccount(account);
		receipt.setDate(date);

		int installment = editView.getInstallment();

		String originalName = receipt.getName();
		if(installment == 1)
			receipt.setName(originalName);
		else
			receipt.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, 1, installment));

		OperationResult result = repository.save(receipt);

		if(result.isValid()) {
			int repetition = installment;
			if(repetition == 1)
				repetition = editView.getRepetition();
			for(int i = 1; i < repetition; i++) {
				if(installment != 1)
					receipt.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i + 1, installment));
				receipt.repeat();
				receipt.save();
			}

			editView.finishView();
		} else {
			for (ValidationError validationError : result.getErrors())
				editView.showError(validationError);
		}
	}

	String getUuid() {
		return receipt != null ? receipt.getUuid() : null;
	}

	void storeBundle(@NonNull  Bundle extras) {
		if(extras.containsKey(KEY_RECEIPT_UUID))
			loadReceipt(extras.getString(KEY_RECEIPT_UUID));
		if(extras.containsKey(KEY_SOURCE_UUID))
			source = sourceRepository.find(extras.getString(KEY_SOURCE_UUID));
		if(extras.containsKey(KEY_ACCOUNT_UUID))
			account = accountRepository.find(extras.getString(KEY_ACCOUNT_UUID));
	}

	void onSaveInstanceState(Bundle outState) {
		if(receipt != null)
			outState.putString(KEY_RECEIPT_UUID, receipt.getUuid());
		if(source != null)
			outState.putString(KEY_SOURCE_UUID, source.getUuid());
		if(account != null)
			outState.putString(KEY_ACCOUNT_UUID, account.getUuid());
	}

	void onSourceSelected(String sourceUuid) {
		source = sourceRepository.find(sourceUuid);
		checkEditViewSet();
		editView.onSourceSelected(source);
	}

	void onAccountSelected(String accountUuid) {
		account = accountRepository.find(accountUuid);
		checkEditViewSet();
		editView.onAccountSelected(account);
	}

	boolean hasReceipt() {
		return receipt != null;
	}

	void onDate(Context ctx) {
		checkEditViewSet();
		DateTime time = date;
		if(time == null)
			time = DateTime.now();
		new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if(date == null)
					date = DateTime.now();
				date = date.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				editView.onDateChanged(date);
			}
		}, time.getYear(), time.getMonthOfYear() - 1, time.getDayOfMonth()).show();
	}
}