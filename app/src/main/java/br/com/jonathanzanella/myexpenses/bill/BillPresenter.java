package br.com.jonathanzanella.myexpenses.bill;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

class BillPresenter {
	private BillContract.View view;
	private BillContract.EditView editView;
	private BillRepository repository;
	private Bill bill;
	private DateTime initDate;
	private DateTime endDate;

	BillPresenter(BillRepository repository) {
		this.repository = repository;
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

	void viewUpdated(boolean invalidateCache) {
		if (bill != null) {
			if(invalidateCache) {
				repository.find(bill.getUuid()).subscribe(new Subscriber<Bill>("BillPresenter.viewUpdated") {
					@Override
					public void onNext(Bill bill) {
						BillPresenter.this.bill = bill;
						updateView();
					}
				});
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

	void loadBill(final String uuid) {
		final CountingIdlingResource idlingResource = new CountingIdlingResource("BillPresenter.loadBill");
		idlingResource.increment();
		repository.find(uuid).subscribe(new Subscriber<Bill>("BillPresenter.loadBill") {
			@Override
			public void onNext(Bill bill) {
				BillPresenter.this.bill = bill;
				if(bill == null)
					throw new BillNotFoundException(uuid);

				updateView();
				idlingResource.decrement();
			}
		});
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
