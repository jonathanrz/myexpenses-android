package br.com.jonathanzanella.myexpenses.expense;

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

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.chargeable.ListChargeableActivity;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.validations.ValidationResult;

import static android.app.Activity.RESULT_OK;

class ExpensePresenter {
	static final String KEY_EXPENSE_UUID = "KeyExpenseUuid";
	private static final int REQUEST_EDIT_EXPENSE = 1;
	private static final String KEY_BILL_UUID = "KeyBillUuid";
	private static final String KEY_DATE = "KeyDate";

	private final ExpenseRepository repository;
	private final BillRepository billRepository;

	private ExpenseContract.View view;
	private ExpenseContract.EditView editView;
	private Expense expense;
	private DateTime date;
	private Bill bill;
	private Chargeable chargeable;

	ExpensePresenter(ExpenseRepository repository, BillRepository billRepository) {
		this.repository = repository;
		this.billRepository = billRepository;
	}

	private void resetCache() {
		date = null;
		bill = null;
		chargeable = null;
		expense = null;
	}

	void attachView(ExpenseContract.View view) {
		this.view = view;
	}

	void attachView(ExpenseContract.EditView view) {
		this.view = view;
		this.editView = view;
	}

	void detachView() {
		this.view = null;
		this.editView = null;
	}

	@UiThread
	void onViewUpdated(final boolean invalidateCache) {
		if (expense != null) {
			if(invalidateCache) {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... voids) {
						expense = repository.find(expense.getUuid());
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
		} else {
			updateView();
		}
	}

	private void updateView() {
		if (expense != null) {
			if (editView != null) {
				editView.setTitle(R.string.edit_expense_title);
			} else {
				String title = view.getContext().getString(R.string.expense);
				view.setTitle(title.concat(" ").concat(expense.getName()));
			}
			view.showExpense(expense);

			loadBill();
			loadChargeable();

			if(date == null)
				date = expense.getDate();
			if (editView != null && date != null)
				editView.onDateChanged(date);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_expense_title);

			if(date == null)
				date = DateTime.now();
			if(editView != null && date != null)
				editView.onDateChanged(date);
		}
	}

	private void loadChargeable() {
		new AsyncTask<Void, Void, Chargeable>() {

			@Override
			protected Chargeable doInBackground(Void... voids) {
				chargeable = expense.getChargeableFromCache();
				return chargeable;
			}

			@Override
			protected void onPostExecute(Chargeable chargeable) {
				super.onPostExecute(chargeable);
				if(editView != null && chargeable != null)
					editView.onChargeableSelected(chargeable);
			}
		}.execute();
	}

	private void loadBill() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				bill = expense.getBill();
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				if(editView != null && bill != null)
					editView.onBillSelected(bill);
			}
		}.execute();
	}

	@UiThread
	void refreshExpense() {
		if(expense != null) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... voids) {
					loadExpense(expense.getUuid());
					return null;
				}

				@Override
				protected void onPostExecute(Void aVoid) {
					super.onPostExecute(aVoid);
					updateView();
				}
			}.execute();
		}
	}

	@WorkerThread
	Expense loadExpense(String uuid) {
		resetCache();
		expense = repository.find(uuid);
		if(expense == null)
			throw new ExpenseNotFoundException(uuid);
		date = expense.getDate();
		return expense;
	}

	private void checkEditViewSet() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
	}

	@UiThread
	void save() {
		checkEditViewSet();
		if(expense == null)
			expense = new Expense();
		expense = editView.fillExpense(expense);
		if(date != null)
			expense.setDate(date);
		if(bill != null)
			expense.setBill(bill);
		if(chargeable != null)
			expense.setChargeable(chargeable);
		final String originalName = expense.getName();
		if(expense.getInstallments() != 1) {
			expense.setName(expense.formatExpenseName(expense.getName(), 1));
			expense.setValue(expense.getValue() / expense.getInstallments());
			expense.setValueToShowInOverview(expense.getValueToShowInOverview() / expense.getInstallments());
		}

		new AsyncTask<Void, Void, ValidationResult>() {
			@Override
			protected ValidationResult doInBackground(Void... voids) {
				ValidationResult result = repository.save(expense);
				if(result.isValid())
					generateExpensesRepetition();

				return result;
			}

			private void generateExpensesRepetition() {
				for (int i = 1; i < expense.getRepetition(); i++) {
					expense = expense.repeat(originalName, i + 1);
					ValidationResult repetitionResult = repository.save(expense);
					if(!repetitionResult.isValid())
						Log.Companion.error("ExpensePresenter", "Error saving repetition of expense " + expense.getData() +
								" error=" + repetitionResult.getErrors().toString());
				}
			}

			@Override
			protected void onPostExecute(ValidationResult result) {
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
	void edit(Activity act) {
		Intent i = new Intent(act, EditExpenseActivity.class);
		i.putExtra(EditExpenseActivity.Companion.getKEY_EXPENSE_UUID(), getUuid());
		act.startActivityForResult(i, REQUEST_EDIT_EXPENSE);
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

						expense.uncharge();
						expense.delete();
						Intent i = new Intent();
						act.setResult(RESULT_OK, i);
						act.finish();
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

	@UiThread
	void onChargeableSelected(final ChargeableType type, final String uuid) {
		new AsyncTask<Void, Void, Chargeable>() {

			@Override
			protected Chargeable doInBackground(Void... voids) {
				chargeable = Expense.Companion.findChargeable(type, uuid);
				return chargeable;
			}

			@Override
			protected void onPostExecute(Chargeable chargeable) {
				super.onPostExecute(chargeable);
				if(chargeable != null)
					editView.onChargeableSelected(chargeable);
			}
		}.execute();
	}

	@UiThread
	void onBillSelected(final String uuid) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				bill = new BillRepository(new RepositoryImpl<Bill>(MyApplication.getContext()), repository).find(uuid);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				if(bill != null)
					editView.onBillSelected(bill);
			}
		}.execute();
	}

	String getUuid() {
		return expense != null ? expense.getUuid() : null;
	}

	@UiThread
	void storeBundle(@NonNull final Bundle extras) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				if(extras.containsKey(KEY_EXPENSE_UUID))
					loadExpense(extras.getString(KEY_EXPENSE_UUID));
				if(extras.containsKey(KEY_BILL_UUID))
					bill = billRepository.find(extras.getString(KEY_BILL_UUID));
				if(extras.containsKey(KEY_DATE))
					date = new DateTime(extras.getLong(KEY_DATE));
				String key = ListChargeableActivity.Companion.getKEY_CHARGEABLE_SELECTED_TYPE();
				if(extras.containsKey(key)) {
					String selectedUuid = extras.getString(ListChargeableActivity.Companion.getKEY_CHARGEABLE_SELECTED_UUID());
					chargeable = Expense.Companion.findChargeable((ChargeableType) extras.getSerializable(key), selectedUuid);
				}
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
		if(expense != null)
			outState.putString(KEY_EXPENSE_UUID, expense.getUuid());
		if(bill != null)
			outState.putString(KEY_BILL_UUID, bill.getUuid());
		if(date != null)
			outState.putLong(KEY_DATE, date.getMillis());
		if(chargeable != null) {
			outState.putString(ListChargeableActivity.Companion.getKEY_CHARGEABLE_SELECTED_UUID(), chargeable.getUuid());
			outState.putSerializable(ListChargeableActivity.Companion.getKEY_CHARGEABLE_SELECTED_TYPE(), chargeable.getChargeableType());
		}
	}

	@UiThread
	public void onActivityResult(int requestCode, int resultCode) {
		switch (requestCode) {
			case REQUEST_EDIT_EXPENSE: {
				if(resultCode == Activity.RESULT_OK)
					refreshExpense();
			}
		}
	}

	private boolean hasExpense() {
		return expense != null;
	}

	@WorkerThread
	boolean hasChargeable() {
		return hasExpense() && expense.getChargeableFromCache() != null;
	}

	@UiThread
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