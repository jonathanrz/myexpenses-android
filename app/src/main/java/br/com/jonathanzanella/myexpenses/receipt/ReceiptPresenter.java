package br.com.jonathanzanella.myexpenses.receipt;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.source.Source;
import br.com.jonathanzanella.myexpenses.source.SourceRepository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static android.app.Activity.RESULT_OK;

class ReceiptPresenter {
	static final String KEY_RECEIPT_UUID = "KeyReceiptUuid";
	private static final String KEY_SOURCE_UUID = "KeySourceUuid";
	private static final String KEY_ACCOUNT_UUID = "KeyAccountUuid";
	private static final String KEY_DATE = "KeyDate";

	private final ReceiptRepository repository;
	private final SourceRepository sourceRepository;
	private final AccountRepository accountRepository;
	private ReceiptContract.View view;
	private ReceiptContract.EditView editView;
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

	@UiThread
	void viewUpdated(boolean invalidateCache) {
		if (receipt != null || invalidateCache) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... voids) {
					receipt = repository.find(receipt.getUuid());
					source = receipt.getSource();
					account = receipt.getAccount();
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {
					super.onPostExecute(aVoid);
					updateView();
				}
			}.execute();
		} else {
			updateView();
		}
	}

	private void updateView() {
		if (receipt != null) {
			if (editView != null) {
				editView.setTitle(R.string.edit_receipt_title);
			} else {
				String title = view.getContext().getString(R.string.receipt);
				view.setTitle(title.concat(" ").concat(receipt.getName()));
			}
			view.showReceipt(receipt);

			if (source != null)
				onSourceSelected(source.getUuid());
			if (account != null)
				onAccountSelected(account.getUuid());

			if(date == null)
				date = receipt.getDate();
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_receipt_title);

			if(date == null)
				date = DateTime.now();
		}

		if(editView != null && date != null)
			editView.onDateChanged(date);
	}

	@UiThread
	void refreshReceipt() {
		new AsyncTask<Void, Void, Receipt>() {

			@Override
			protected Receipt doInBackground(Void... voids) {
				String uuid = receipt.getUuid();
				receipt = repository.find(uuid);
				if(receipt == null)
					throw new ReceiptNotFoundException(uuid);
				return receipt;
			}

			@Override
			protected void onPostExecute(Receipt receipt) {
				super.onPostExecute(receipt);
				view.showReceipt(receipt);
			}
		}.execute();
	}

	@WorkerThread
	void loadReceipt(String uuid) {
		receipt = repository.find(uuid);
		if(receipt == null)
			throw new ReceiptNotFoundException(uuid);
	}

	private void checkEditViewSet() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
	}

	@UiThread
	void save() {
		checkEditViewSet();
		if(receipt == null)
			receipt = new Receipt();
		receipt = editView.fillReceipt(receipt);
		if(date != null)
			receipt.setDate(date);
		if(source != null)
			receipt.setSource(source);
		if(account != null)
			receipt.setAccount(account);

		final String originalName = receipt.getName();
		if(receipt.getInstallments() != 1) {
			receipt.setName(receipt.formatReceiptName(receipt.getName(), 1));
			receipt.setIncome(receipt.getIncome() / receipt.getInstallments());
		}

		new AsyncTask<Void, Void, OperationResult>() {

			@Override
			protected OperationResult doInBackground(Void... voids) {
				OperationResult result = repository.save(receipt);
				if(result.isValid())
					generateReceiptsRepetition();

				return result;
			}

			private void generateReceiptsRepetition() {
				for (int i = 1; i < receipt.getRepetition(); i++) {
					receipt = receipt.repeat(originalName, i + 1);
					OperationResult repetitionResult = repository.save(receipt);
					if(!repetitionResult.isValid())
						Log.error("ExpensePresenter", "Error saving repetition of receipt " + receipt.getData() +
								" error=" + repetitionResult.getErrors().toString());
				}
			}

			@Override
			protected void onPostExecute(OperationResult result) {
				super.onPostExecute(result);

				if(result.isValid()) {
					editView.finishView();
				} else {
					for (ValidationError validationError : result.getErrors())
						editView.showError(validationError);
				}
			}
		}.execute();
	}

	@UiThread
	void edit(Context ctx) {
		Intent i = new Intent(ctx, EditReceiptActivity.class);
		i.putExtra(EditReceiptActivity.KEY_RECEIPT_UUID, getUuid());
		ctx.startActivity(i);
	}

	@UiThread
	void delete(final Activity act) {
		new AlertDialog.Builder(act)
				.setTitle(android.R.string.dialog_alert_title)
				.setMessage(R.string.message_confirm_deletion)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						new AsyncTask<Void, Void, Void>() {

							//TODO: add loading

							@Override
							protected Void doInBackground(Void... voids) {
								Account acc = receipt.getAccount();
								acc.credit(receipt.getIncome() * -1);
								accountRepository.save(acc);

								receipt.delete();
								return null;
							}

							@Override
							protected void onPostExecute(Void aVoid) {
								super.onPostExecute(aVoid);
								Intent i = new Intent();
								act.setResult(RESULT_OK, i);
								act.finish();
							}
						}.execute();

					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
	}

	String getUuid() {
		return receipt != null ? receipt.getUuid() : null;
	}

	@UiThread
	void storeBundle(@NonNull final Bundle extras) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids) {
				if(extras.containsKey(KEY_SOURCE_UUID))
					source = sourceRepository.find(extras.getString(KEY_SOURCE_UUID));

				if(extras.containsKey(KEY_RECEIPT_UUID))
					loadReceipt(extras.getString(KEY_RECEIPT_UUID));

				if(extras.containsKey(KEY_ACCOUNT_UUID))
					account = accountRepository.find(extras.getString(KEY_ACCOUNT_UUID));

				if(extras.containsKey(KEY_DATE))
					date = new DateTime(extras.getLong(KEY_DATE));
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				updateView();
			}
		}.execute();
	}

	void onSaveInstanceState(Bundle outState) {
		if(receipt != null)
			outState.putString(KEY_RECEIPT_UUID, receipt.getUuid());
		if(source != null)
			outState.putString(KEY_SOURCE_UUID, source.getUuid());
		if(account != null)
			outState.putString(KEY_ACCOUNT_UUID, account.getUuid());
		if(date != null)
			outState.putLong(KEY_DATE, date.getMillis());
	}

	void onSourceSelected(final String sourceUuid) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				source = sourceRepository.find(sourceUuid);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				if(editView != null)
					editView.onSourceSelected(source);
			}
		}.execute();
	}

	@UiThread
	void onAccountSelected(final String accountUuid) {
		new AsyncTask<Void, Void, Account>() {

			@Override
			protected Account doInBackground(Void... voids) {
				account = accountRepository.find(accountUuid);
				return account;
			}

			@Override
			protected void onPostExecute(Account account) {
				super.onPostExecute(account);
				if(editView != null)
					editView.onAccountSelected(account);
			}
		}.execute();
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