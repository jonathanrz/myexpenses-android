package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.bill.ListBillActivity;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.chargeable.ListChargeableActivity;
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
public class EditExpenseActivity extends BaseActivity implements ExpenseContract.EditView {
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

	private ExpensePresenter presenter = new ExpensePresenter(new ExpenseRepository(), new BillRepository());

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
		presenter.attachView(this);
		presenter.viewUpdated(false);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras != null)
			presenter.storeBundle(extras);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		presenter.onSaveInstanceState(outState);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		presenter.attachView(this);
		switch (requestCode) {
			case REQUEST_SELECT_CHARGEABLE: {
				if(resultCode == RESULT_OK) {
					presenter.onChargeableSelected(
							(ChargeableType) data.getSerializableExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_TYPE),
							data.getStringExtra(ListChargeableActivity.KEY_CHARGEABLE_SELECTED_UUID));
				}
				break;
			}
			case REQUEST_SELECT_BILL: {
				if(resultCode == RESULT_OK)
					presenter.onBillSelected(data.getStringExtra(ListBillActivity.KEY_BILL_SELECTED_UUID));
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
				presenter.save();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@OnClick(R.id.act_edit_expense_date)
	void onBalanceDate() {
		presenter.onDate(this);
	}

	@Override
	public void onDateChanged(DateTime date) {
		editDate.setText(Expense.sdf.format(date.toDate()));
	}

	@Override
	public void onChargeableSelected(Chargeable chargeable) {
		editChargeable.setText(chargeable.getName());
		checkPayNextMonth.setVisibility(chargeable.canBePaidNextMonth() ? View.VISIBLE : View.GONE);
	}

	@OnClick(R.id.act_edit_expense_chargeable)
	void onChargeable() {
		if(!presenter.hasChargeable())
			startActivityForResult(new Intent(this, ListChargeableActivity.class), REQUEST_SELECT_CHARGEABLE);
	}

	@Override
	public void onBillSelected(Bill bill) {
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

	@Override
	public Expense fillExpense(Expense expense) {
		expense.setName(editName.getText().toString());
		int value = 0;
		String valueText = editValue.getText().toString().replaceAll("[^\\d]", "");
		if(!StringUtils.isEmpty(valueText))
			value = Integer.parseInt(valueText) / getInstallment();

		int valueToShowInOverview = 0;
		String valueToShowInOverviewText = editValueToShowInOverview.getText().toString().replaceAll("[^\\d]", "");
		if(!StringUtils.isEmpty(valueToShowInOverviewText))
			valueToShowInOverview = Integer.parseInt(valueToShowInOverviewText) / getInstallment();

		if (checkRepayment.isChecked()) {
			value *= -1;
			valueToShowInOverview *= -1;
		}
		expense.setValue(value);
		expense.setValueToShowInOverview(valueToShowInOverview);
		expense.setChargeNextMonth(checkPayNextMonth.isChecked());
		expense.showInOverview(showInOverview.isChecked());
		expense.showInResume(showInResume.isChecked());
		expense.setUserUuid(selectUserView.getSelectedUser());
		return expense;
	}

	@Override
	public void finishView() {
		Intent i = new Intent();
		i.putExtra(KEY_EXPENSE_UUID, presenter.getUuid());
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
				editValue.setError(getString(error.getMessage()));
				break;
			case CHARGEABLE:
				editChargeable.setError(getString(error.getMessage()));
				break;
			default:
				Log.error(this.getClass().getName(), "Validation unrecognized, field:" + error);
		}
	}

	@Override
	public int getInstallment() {
		return Integer.parseInt(editInstallment.getText().toString());
	}

	@Override
	public int getRepetition() {
		return Integer.parseInt(editRepetition.getText().toString());
	}

	@Override
	public void showExpense(Expense expense) {
		editName.setText(expense.getName());
		editValue.setText(NumberFormat.getCurrencyInstance().format(Math.abs(expense.getValue()) / 100.0));
		editValueToShowInOverview.setText(NumberFormat.getCurrencyInstance().format(Math.abs(expense.getValueToShowInOverview()) / 100.0));
		if(expense.isCharged()) {
			editValue.setTextColor(getResources().getColor(R.color.value_unpaid));
			checkRepayment.setEnabled(false);
		}
		if(expense.getValue() < 0)
			checkRepayment.setChecked(true);

		checkPayNextMonth.setChecked(expense.isChargeNextMonth());
		showInOverview.setChecked(expense.isShowInOverview());
		showInResume.setChecked(expense.isShowInResume());

		selectUserView.setSelectedUser(expense.getUserUuid());
	}
}
