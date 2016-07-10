package br.com.jonathanzanella.myexpenses.expense;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import br.com.jonathanzanella.myexpenses.chargeable.ListChargeableActivity;
import br.com.jonathanzanella.myexpenses.bill.ListBillActivity;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditExpenseActivity extends BaseActivity {
	public static final String KEY_EXPENSE_UUID = "KeyReceiptUuid";
	private static final int REQUEST_SELECT_CHARGEABLE = 1003;
	private static final int REQUEST_SELECT_BILL = 1004;

	@Bind(R.id.act_edit_expense_name)
	EditText editName;
	@Bind(R.id.act_edit_expense_date)
	EditText editDate;
	@Bind(R.id.act_edit_expense_value)
	EditText editValue;
	@Bind(R.id.act_edit_expense_value_to_show_in_overview)
	EditText editValueToShowInOverview;
	@Bind(R.id.act_edit_expense_repayment)
	CheckBox checkRepayment;
	@Bind(R.id.act_edit_expense_chargeable)
	EditText editChargeable;
	@Bind(R.id.act_edit_expense_bill)
	EditText editBill;
	@Bind(R.id.act_edit_expense_pay_next_month)
	CheckBox checkPayNextMonth;
	@Bind(R.id.act_edit_expense_show_in_overview)
	CheckBox showInOverview;
	@Bind(R.id.act_edit_expense_show_in_resume)
	CheckBox showInResume;
	@Bind(R.id.act_edit_expense_repetition)
	EditText editRepetition;
	@Bind(R.id.act_edit_expense_installment)
	EditText editInstallment;
	@Bind(R.id.act_edit_expense_user)
	SelectUserView selectUserView;

	private Expense expense;
	private DateTime date;
	private Chargeable chargeable;
	private Bill bill;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_expense);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		editValue.addTextChangedListener(new CurrencyTextWatch(editValue));
		editValueToShowInOverview.addTextChangedListener(new CurrencyTextWatch(editValueToShowInOverview));
		editValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(editValueToShowInOverview.getText().toString().isEmpty() &&
						!editValue.getText().toString().isEmpty()) {
					editValueToShowInOverview.setText(editValue.getText());
				}
			}
		});

		if(expense != null) {
			setData();
		} else {
			initData();
		}
	}

	private void initData() {
		date = DateTime.now();
		onBalanceDateChanged();
		if(chargeable != null)
			onChargeableSelected();
	}

	private void setData() {
		editName.setText(expense.getName());
		editValue.setText(NumberFormat.getCurrencyInstance().format(Math.abs(expense.getValue()) / 100.0));
		editValueToShowInOverview.setText(NumberFormat.getCurrencyInstance().format(Math.abs(expense.getValueToShowInOverview()) / 100.0));
		if(expense.isCharged()) {
			editValue.setTextColor(getColor(R.color.value_unpaid));
			checkRepayment.setEnabled(false);
		}
		if(expense.getValue() < 0)
			checkRepayment.setChecked(true);
		chargeable = expense.getChargeable();
		if(chargeable != null) {
			editChargeable.setText(chargeable.getName());
			onChargeableSelected();
		}
		checkPayNextMonth.setChecked(expense.isChargeNextMonth());
		showInOverview.setChecked(expense.isShowInOverview());
		showInResume.setChecked(expense.isShowInResume());
		date = expense.getDate();
		if(date == null)
			date = DateTime.now();
		onBalanceDateChanged();
		selectUserView.setSelectedUser(expense.getUserUuid());
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras == null)
			return;

		if(extras.containsKey(KEY_EXPENSE_UUID))
			expense = Expense.find(extras.getString(KEY_EXPENSE_UUID));

		if(extras.containsKey(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE)) {
			chargeable = Expense.findChargeable((ChargeableType) extras.getSerializable(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE),
										extras.getString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(expense != null)
			outState.putString(KEY_EXPENSE_UUID, expense.getUuid());
		if(chargeable != null) {
			outState.putString(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID, chargeable.getUuid());
			outState.putSerializable(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE, chargeable.getChargeableType());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_SELECT_CHARGEABLE: {
				if(resultCode == RESULT_OK) {
					chargeable = Expense.findChargeable(
							(ChargeableType) data.getSerializableExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE),
							data.getStringExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID));
					if(chargeable != null)
						onChargeableSelected();
				}
				break;
			}
			case REQUEST_SELECT_BILL: {
				if(resultCode == RESULT_OK) {
					bill = Bill.find(data.getStringExtra(ListBillActivity.KEY_BILL_SELECTED_UUID));
					onBillSelected();
				}
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
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

	@OnClick(R.id.act_edit_expense_date)
	void onBalanceDate() {
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				date = date.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
				onBalanceDateChanged();
			}
		}, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth()).show();
	}

	private void onBalanceDateChanged() {
		editDate.setText(Receipt.sdf.format(date.toDate()));
	}

	private void onChargeableSelected() {
		editChargeable.setText(chargeable.getName());
		checkPayNextMonth.setVisibility(chargeable.canBePaidNextMonth() ? View.VISIBLE : View.GONE);
	}

	@OnClick(R.id.act_edit_expense_chargeable)
	void onChargeable() {
		if(expense == null || expense.getChargeable() == null)
			startActivityForResult(new Intent(this, ListChargeableActivity.class), REQUEST_SELECT_CHARGEABLE);
	}

	private void onBillSelected() {
		if(bill != null) {
			if(editBill.getText().toString().isEmpty())
				editBill.setText(bill.getName());
			if(editName.getText().toString().isEmpty())
				editName.setText(bill.getName());
			if(editValue.getText().toString().isEmpty())
				editValue.setText(NumberFormat.getCurrencyInstance().format(bill.getAmount() / 100.0));
			showInOverview.setChecked(false);
			showInResume.setChecked(true);
		} else {
			editBill.setText("");
		}
	}

	@OnClick(R.id.act_edit_expense_bill)
	void onBill() {
		startActivityForResult(new Intent(this, ListBillActivity.class), REQUEST_SELECT_BILL);
	}

	private void save() {
		int installment = Integer.parseInt(editInstallment.getText().toString());
		if(expense == null)
			expense = new Expense();
		String originalName = editName.getText().toString();
		if(installment == 1)
			expense.setName(originalName);
		else
			expense.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, 1, installment));
		expense.setDate(date);
		int value = Integer.parseInt(editValue.getText().toString().replaceAll("[^\\d]", "")) / installment;
		int valueToShowInOverview = Integer.parseInt(editValueToShowInOverview.getText().toString().replaceAll("[^\\d]", "")) / installment;
		if(checkRepayment.isChecked()) {
			value *= -1;
			valueToShowInOverview *= -1;
		}
		expense.setValue(value);
		expense.setValueToShowInOverview(valueToShowInOverview);
		expense.setChargeable(chargeable);
		expense.setBill(bill);
		expense.setChargeNextMonth(checkPayNextMonth.isChecked());
		expense.showInOverview(showInOverview.isChecked());
		expense.showInResume(showInResume.isChecked());
		expense.setUserUuid(selectUserView.getSelectedUser());

		if(expense.isCharged() && date.isAfterNow())
			expense.uncharge();
		expense.save();

		int repetition = installment;
		if(repetition == 1)
			repetition = Integer.parseInt(editRepetition.getText().toString());
		for(int i = 1; i < repetition; i++) {
			if(installment != 1)
				expense.setName(String.format(Environment.PTBR_LOCALE, "%s %02d/%02d", originalName, i + 1, installment));
			expense.repeat();
			expense.save();
		}

		Intent i = new Intent();
		i.putExtra(KEY_EXPENSE_UUID, expense.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
