package br.com.jonathanzanella.myexpenses.bill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditBillActivity extends BaseActivity {
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

	private Bill bill;
	private DateTime initDate;
	private DateTime endDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_bill);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		editAmount.addTextChangedListener(new CurrencyTextWatch(editAmount));

		initDate = DateTime.now();
		endDate = DateTime.now();

		if(bill != null) {
			editName.setText(bill.getName());
			editAmount.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
			editDueDate.setText(String.valueOf(bill.getDueDate()));
			initDate = bill.getInitDate();
			onInitDateChanged();
			endDate = bill.getEndDate();
			onEndDateChanged();
			selectUserView.setSelectedUser(bill.getUserUuid());
		} else {
			onInitDateChanged();
			onEndDateChanged();
		}
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_BILL_UUID))
			bill = Bill.find(extras.getString(KEY_BILL_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(bill != null)
			outState.putString(KEY_BILL_UUID, bill.getUuid());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}

	@OnClick(R.id.act_edit_bill_init_date)
	void onInitDate() {
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				initDate = initDate.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				onInitDateChanged();
			}
		}, initDate.getYear(), initDate.getMonthOfYear() - 1, initDate.getDayOfMonth()).show();
	}

	private void onInitDateChanged() {
		editInitDate.setText(Bill.sdf.format(initDate.toDate()));
	}

	@OnClick(R.id.act_edit_bill_end_date)
	void onEndDate() {
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				endDate = endDate.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				onEndDateChanged();
			}
		}, endDate.getYear(), endDate.getMonthOfYear() - 1, endDate.getDayOfMonth()).show();
	}

	private void onEndDateChanged() {
		editEndDate.setText(Bill.sdf.format(endDate.toDate()));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_save:
				save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void save() {
		if(bill == null)
			bill = new Bill();
		bill.setName(editName.getText().toString());
		bill.setAmount(Integer.parseInt(editAmount.getText().toString().replaceAll("[^\\d]", "")));
		bill.setDueDate(Integer.parseInt(editDueDate.getText().toString()));
		bill.setInitDate(initDate);
		bill.setEndDate(endDate);
		bill.setUserUuid(selectUserView.getSelectedUser());
		bill.save();

		Intent i = new Intent();
		i.putExtra(KEY_BILL_UUID, bill.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
