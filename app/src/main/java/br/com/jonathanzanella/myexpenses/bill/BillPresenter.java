package br.com.jonathanzanella.myexpenses.bill;

import android.support.annotation.Nullable;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

/**
 * Created by jzanella on 8/27/16.
 */

class BillPresenter {
	private BillContract.View view;
	@Nullable
	private BillContract.EditView editView;
	private BillRepository repository;
	private Bill bill;

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
			if(invalidateCache)
				bill = repository.find(bill.getUuid());
			if(editView != null) {
				editView.setTitle(R.string.edit_bill_title);
			} else {
				String title = view.getContext().getString(R.string.bill);
				view.setTitle(title.concat(" ").concat(bill.getName()));
			}
			view.showBill(bill);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_bill_title);
		}
	}

	void loadBill(String uuid) {
		bill = repository.find(uuid);
		if(bill == null)
			throw new BillNotFoundException(uuid);
	}

	void save() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
		if(bill == null)
			bill = new Bill();
		bill = editView.fillBill(bill);
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
