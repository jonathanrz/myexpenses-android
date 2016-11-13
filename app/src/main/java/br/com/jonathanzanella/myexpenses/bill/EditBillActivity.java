package br.com.jonathanzanella.myexpenses.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditBillActivity extends BaseActivity implements BillContract.EditView {
	public static final String KEY_BILL_UUID = "KeyBillUuid";

	@Bind(R.id.act_edit_bill_name)
	EditText editName;
	@Bind(R.id.act_edit_bill_amount)
	EditText editAmount;
	@Bind(R.id.act_edit_bill_due_date)
	EditText editDueDate;
	@Bind(R.id.act_edit_bill_init_date)
	EditText editInitDate;
	@Bind(R.id.act_edit_bill_end_date)
	EditText editEndDate;
	@Bind(R.id.act_edit_bill_user)
	SelectUserView selectUserView;

	private BillPresenter presenter;

	public EditBillActivity() {
		presenter = new BillPresenter(new BillRepository(new Repository<Bill>(MyApplication.getContext())));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_bill);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		editAmount.addTextChangedListener(new CurrencyTextWatch(editAmount));
		presenter.viewUpdated(false);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_BILL_UUID))
			presenter.loadBill(extras.getString(KEY_BILL_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String uuid = presenter.getUuid();
		if(uuid != null)
			outState.putString(KEY_BILL_UUID, uuid);
	}

	@Override
	protected void onStart() {
		super.onStart();
		presenter.attachView(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		presenter.detachView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}

	@OnClick(R.id.act_edit_bill_init_date)
	void onInitDate() {
		presenter.onInitDate(this);
	}

	@Override
	public void onInitDateChanged(DateTime date) {
		editInitDate.setText(Bill.sdf.format(date.toDate()));
	}

	@OnClick(R.id.act_edit_bill_end_date)
	void onEndDate() {
		presenter.onEndDate(this);
	}

	@Override
	public void onEndDateChanged(DateTime date) {
		editEndDate.setText(Bill.sdf.format(date.toDate()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_save:
				presenter.save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void showBill(Bill bill) {
		editName.setText(bill.getName());
		editAmount.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
		editDueDate.setText(String.valueOf(bill.getDueDate()));
		selectUserView.setSelectedUser(bill.getUserUuid());
	}

	@Override
	public Bill fillBill(Bill bill) {
		String amountText = editAmount.getText().toString().replaceAll("[^\\d]", "");
		String dueDateText = editDueDate.getText().toString().replaceAll("[^\\d]", "");

		bill.setName(editName.getText().toString());
		bill.setAmount(StringUtils.isEmpty(amountText) ? 0 : Integer.parseInt(amountText));
		bill.setDueDate(StringUtils.isEmpty(dueDateText) ? 0 : Integer.parseInt(dueDateText));
		bill.setUserUuid(selectUserView.getSelectedUser());
		return bill;
	}

	@Override
	public void finishView() {
		Intent i = new Intent();
		i.putExtra(KEY_BILL_UUID, presenter.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public void showError(ValidationError error) {
		switch (error) {
			case NAME:
				editName.setError(getString(error.getMessage()));
				break;
			case AMOUNT:
				editAmount.setError(getString(error.getMessage()));
				break;
			case DUE_DATE:
				editDueDate.setError(getString(error.getMessage()));
				break;
			case INIT_DATE:
			case INIT_DATE_GREATER_THAN_END_DATE:
				editInitDate.setError(getString(error.getMessage()));
				break;
			case END_DATE:
				editEndDate.setError(getString(error.getMessage()));
				break;
			default:
				Log.error(this.getClass().getName(), "Validation unrecognized, field:" + error);
		}
	}
}
