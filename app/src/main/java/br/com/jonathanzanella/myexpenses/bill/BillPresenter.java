package br.com.jonathanzanella.myexpenses.bill;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.helpers.CountingIdlingResource;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

class BillPresenter {
	private BillContract.View view;
	private BillContract.EditView editView;
	private final BillRepository repository;
	private Bill bill;
	private DateTime initDate;
	private DateTime endDate;
	private final CountingIdlingResource idlingResource;

	BillPresenter(BillRepository repository, CountingIdlingResource idlingResource) {
		this.repository = repository;
		this.idlingResource = idlingResource;
	}

	void attachView(BillContract.View view) {
		this.view = view;
	}

	void attachView(BillContract.EditView view) {
		this.view = view;
		this.editView = view;
	}

	void detachView() {
		this.view = null;
		this.editView = null;
	}

	@UiThread
	void onViewUpdated(boolean invalidateCache) {
		if (bill != null) {
			if(invalidateCache) {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... voids) {
						loadBill(bill.getUuid());
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						updateView();
					}
				}.execute();
			}
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_bill_title);
		}
	}

	private void updateView() {
		if(editView != null) {
			editView.setTitle(R.string.edit_bill_title);
		} else {
			String title = view.getContext().getString(R.string.bill);
			view.setTitle(title.concat(" ").concat(bill.getName()));
		}
		view.showBill(bill);
		initDate = bill.getInitDate();
		if(editView != null)
			editView.onInitDateChanged(initDate);
		endDate = bill.getEndDate();
		if(editView != null)
			editView.onEndDateChanged(endDate);
	}

	void onInitDate(Context ctx) {
		checkEditViewSet();
		DateTime time = initDate;
		if(time == null)
			time = DateTime.now();
		new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if(initDate == null)
					initDate = DateTime.now();
				initDate = initDate.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				editView.onInitDateChanged(initDate);
			}
		}, time.getYear(), time.getMonthOfYear() - 1, time.getDayOfMonth()).show();
	}

	void onEndDate(Context ctx) {
		checkEditViewSet();
		DateTime time = initDate;
		if(time == null)
			time = DateTime.now();
		new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if(endDate == null)
					endDate = DateTime.now();
				endDate = endDate.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				editView.onEndDateChanged(endDate);
			}
		}, time.getYear(), time.getMonthOfYear() - 1, time.getDayOfMonth()).show();
	}

	@WorkerThread
	void loadBill(final String uuid) {
		bill = repository.find(uuid);
	}

	private void checkEditViewSet() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
	}

	void save() {
		checkEditViewSet();
		if(bill == null)
			bill = new Bill();
		bill = editView.fillBill(bill);
		bill.setInitDate(initDate);
		bill.setEndDate(endDate);
		OperationResult result = repository.save(bill);

		if(result.isValid()) {
			editView.finishView();
		} else {
			for (ValidationError validationError : result.getErrors())
				editView.showError(validationError);
		}
	}

	String getUuid() {
		return bill != null ? bill.getUuid() : null;
	}
}
