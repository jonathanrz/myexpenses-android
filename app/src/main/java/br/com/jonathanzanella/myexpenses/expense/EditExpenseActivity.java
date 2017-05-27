package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.bill.ListBillActivity;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType;
import br.com.jonathanzanella.myexpenses.chargeable.ListChargeableActivity;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;
import butterknife.OnClick;

public class EditExpenseActivity extends BaseActivity implements ExpenseContract.EditView {
	public static final String KEY_EXPENSE_UUID = ExpensePresenter.KEY_EXPENSE_UUID;
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

	private final ExpensePresenter presenter;

	public EditExpenseActivity() {
		ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(this));
		presenter = new ExpensePresenter(expenseRepository, new BillRepository(new RepositoryImpl<Bill>(this), expenseRepository));
	}

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
		presenter.onViewUpdated(false);
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
	protected void onResume() {
		super.onResume();
		presenter.refreshExpense();
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
					String uuid = data.getStringExtra(ListChargeableActivity.Companion.getKEY_CHARGEABLE_SELECTED_UUID());
					String keyChargeableSelectedType = ListChargeableActivity.Companion.getKEY_CHARGEABLE_SELECTED_TYPE();
					ChargeableType type = (ChargeableType) data.getSerializableExtra(keyChargeableSelectedType);
					presenter.onChargeableSelected(type,uuid);
				}
				break;
			}
			case REQUEST_SELECT_BILL: {
				if(resultCode == RESULT_OK)
					presenter.onBillSelected(data.getStringExtra(ListBillActivity.Companion.getKEY_BILL_SELECTED_UUID()));
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
		editDate.setText(Expense.SIMPLE_DATE_FORMAT.format(date.toDate()));
	}

	@Override
	public void onChargeableSelected(Chargeable chargeable) {
		editChargeable.setText(chargeable.getName());
		checkPayNextMonth.setVisibility(chargeable.canBePaidNextMonth() ? View.VISIBLE : View.GONE);
	}

	@OnClick(R.id.act_edit_expense_chargeable)
	void onChargeable() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... voids) {
				return presenter.hasChargeable();
			}

			@Override
			protected void onPostExecute(Boolean hasChargeable) {
				super.onPostExecute(hasChargeable);
				if(!hasChargeable) {
					Intent intent = new Intent(EditExpenseActivity.this, ListChargeableActivity.class);
					startActivityForResult(intent, REQUEST_SELECT_CHARGEABLE);
				}
			}
		}.execute();
	}

	@Override
	public void onBillSelected(Bill bill) {
		if(bill != null) {
			if(editBill.getText().toString().isEmpty())
				editBill.setText(bill.getName());
			if(editName.getText().toString().isEmpty())
				editName.setText(bill.getName());
			if(editValue.getText().toString().isEmpty())
				editValue.setText(CurrencyHelper.format(bill.getAmount()));
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
			value = Integer.parseInt(valueText);

		int valueToShowInOverview = 0;
		String valueToShowInOverviewText = editValueToShowInOverview.getText().toString().replaceAll("[^\\d]", "");
		if(!StringUtils.isEmpty(valueToShowInOverviewText))
			valueToShowInOverview = Integer.parseInt(valueToShowInOverviewText);

		if (checkRepayment.isChecked()) {
			value *= -1;
			valueToShowInOverview *= -1;
		}
		expense.setValue(value);
		expense.setValueToShowInOverview(valueToShowInOverview);
		expense.setChargedNextMonth(checkPayNextMonth.isChecked());
		expense.showInOverview(showInOverview.isChecked());
		expense.showInResume(showInResume.isChecked());
		expense.setInstallments(getInstallment());
		expense.setRepetition(getRepetition());
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

	public int getInstallment() {
		return Integer.parseInt(editInstallment.getText().toString());
	}

	public int getRepetition() {
		return Integer.parseInt(editRepetition.getText().toString());
	}

	@Override
	public void showExpense(Expense expense) {
		editName.setText(expense.getName());
		editValue.setText(CurrencyHelper.format(Math.abs(expense.getValue())));
		editValueToShowInOverview.setText(CurrencyHelper.format(Math.abs(expense.getValueToShowInOverview())));
		if(expense.isCharged()) {
			//noinspection deprecation
			editValue.setTextColor(getResources().getColor(R.color.value_unpaid));
			checkRepayment.setEnabled(false);
		}
		if(expense.getValue() < 0)
			checkRepayment.setChecked(true);

		checkPayNextMonth.setChecked(expense.isChargedNextMonth());
		showInOverview.setChecked(expense.isShowInOverview());
		showInResume.setChecked(expense.isShowInResume());
	}
}
