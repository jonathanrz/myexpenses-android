package br.com.jonathanzanella.myexpenses.expense;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.chargeable.ListChargeableActivity;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jzanella on 8/27/16.
 */

class ExpensePresenter {
	static final String KEY_EXPENSE_UUID = "KeyExpenseUuid";
	static final String KEY_BILL_UUID = "KeyBillUuid";

	private ExpenseContract.View view;
	private ExpenseContract.EditView editView;
	private ExpenseRepository repository;
	private BillRepository billRepository;
	private Expense expense;
	private DateTime date;
	private Bill bill;
	private Chargeable chargeable;

	ExpensePresenter(ExpenseRepository repository, BillRepository billRepository) {
		this.repository = repository;
		this.billRepository = billRepository;
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

	void viewUpdated(boolean invalidateCache) {
		if (expense != null) {
			if(invalidateCache)
				expense = repository.find(expense.getUuid());
			if(editView != null) {
				editView.setTitle(R.string.edit_expense_title);
			} else {
				String title = view.getContext().getString(R.string.expense);
				view.setTitle(title.concat(" ").concat(expense.getName()));
			}
			view.showExpense(expense);

			bill = expense.getBill();
			if(editView != null && bill != null)
				editView.onBillSelected(bill);

			chargeable = expense.getChargeable();
			if(editView != null && chargeable != null)
				editView.onChargeableSelected(chargeable);

			date = expense.getDate();
			if(editView != null && date != null)
				editView.onDateChanged(date);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_expense_title);

			date = DateTime.now();
			if(editView != null && date != null)
				editView.onDateChanged(date);
		}
	}

	void refreshExpense() {
		loadExpense(expense.getUuid());

		view.showExpense(expense);
	}

	Expense loadExpense(String uuid) {
		expense = repository.find(uuid);
		if(expense == null)
			throw new ExpenseNotFoundException(uuid);
		return expense;
	}

	private void checkEditViewSet() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
	}

	void save() {
		checkEditViewSet();
		if(expense == null)
			expense = new Expense();
		expense = editView.fillExpense(expense);
		expense.setDate(date);
		if(bill != null)
			expense.setBill(bill);
		if(chargeable != null)
			expense.setChargeable(chargeable);

		int installment = editView.getInstallment();

		String originalName = expense.getName();
		if(installment == 1)
			expense.setName(originalName);
		else
			expense.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, 1, installment));

		OperationResult result = repository.save(expense);

		if(result.isValid()) {
			int repetition = installment;
			if(repetition == 1)
				repetition = editView.getRepetition();
			for(int i = 1; i < repetition; i++) {
				if(installment != 1)
					expense.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i + 1, installment));
				expense.repeat();
				expense.save();
			}

			editView.finishView();
		} else {
			for (ValidationError validationError : result.getErrors())
				editView.showError(validationError);
		}
	}

	void edit(Context ctx) {
		Intent i = new Intent(ctx, EditExpenseActivity.class);
		i.putExtra(EditExpenseActivity.KEY_EXPENSE_UUID, getUuid());
		ctx.startActivity(i);
	}

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

	void onChargeableSelected(ChargeableType type, String uuid) {
		chargeable = Expense.findChargeable(type, uuid);
		if(chargeable != null)
			editView.onChargeableSelected(chargeable);
	}

	void onBillSelected(String uuid) {
		bill = new BillRepository(new Repository<Bill>(MyApplication.getContext())).find(uuid);
		if(bill != null)
			editView.onBillSelected(bill);
	}

	String getUuid() {
		return expense != null ? expense.getUuid() : null;
	}

	void storeBundle(@NonNull  Bundle extras) {
		if(extras.containsKey(KEY_EXPENSE_UUID))
			loadExpense(extras.getString(KEY_EXPENSE_UUID));
		if(extras.containsKey(KEY_BILL_UUID))
			bill = billRepository.find(extras.getString(KEY_BILL_UUID));
		if(extras.containsKey(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE)) {
			chargeable = Expense.findChargeable(
					(ChargeableType) extras.getSerializable(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE),
					extras.getString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID));
		}
	}

	void onSaveInstanceState(Bundle outState) {
		if(expense != null)
			outState.putString(KEY_EXPENSE_UUID, expense.getUuid());
		if(bill != null)
			outState.putString(KEY_BILL_UUID, bill.getUuid());
		if(chargeable != null) {
			outState.putString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID, chargeable.getUuid());
			outState.putSerializable(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE, chargeable.getChargeableType());
		}
	}

	boolean hasExpense() {
		return expense != null;
	}

	boolean hasChargeable() {
		return hasExpense() && expense.getChargeable() != null;
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