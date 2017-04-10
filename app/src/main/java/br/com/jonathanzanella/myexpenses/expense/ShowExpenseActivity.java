package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.BindView;

public class ShowExpenseActivity extends BaseActivity implements ExpenseContract.View {
	public static final String KEY_EXPENSE_UUID = ExpensePresenter.KEY_EXPENSE_UUID;

	@BindView(R.id.act_show_expense_name)
	TextView expenseName;
	@BindView(R.id.act_show_expense_date)
	TextView expenseDate;
	@BindView(R.id.act_show_expense_value)
	TextView expenseIncome;
	@BindView(R.id.act_show_expense_value_to_show_in_overview)
	TextView expenseIncomeToShowInOverview;
	@BindView(R.id.act_show_expense_chargeable)
	TextView expenseChargeable;
	@BindView(R.id.act_show_expense_charge_next_month)
	TableRow chargeNextMonth;

	private ExpensePresenter presenter;

	@UiThread
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		ExpenseRepository expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(this));
		presenter = new ExpensePresenter(expenseRepository, new BillRepository(new RepositoryImpl<Bill>(this), expenseRepository));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_expenses);
	}

	@UiThread
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		presenter.onViewUpdated(false);
	}

	@UiThread
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

	@UiThread
	@Override
	protected void onStart() {
		super.onStart();
		presenter.attachView(this);
	}

	@UiThread
	@Override
	protected void onStop() {
		super.onStop();
		presenter.detachView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_delete, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit: {
				presenter.edit(this);
				break;
			}
			case R.id.action_delete: {
				presenter.delete(this);

				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@UiThread
	@Override
	public void showExpense(final Expense expense) {
		expenseName.setText(expense.getName());
		expenseDate.setText(Receipt.SIMPLE_DATE_FORMAT.format(expense.getDate().toDate()));
		expenseIncome.setText(CurrencyHelper.format(expense.getValue()));
		expenseIncomeToShowInOverview.setText(CurrencyHelper.format(expense.getValueToShowInOverview()));
		new AsyncTask<Void, Void, Chargeable>() {

			@Override
			protected Chargeable doInBackground(Void... voids) {
				return expense.getChargeableFromCache();
			}

			@Override
			protected void onPostExecute(Chargeable chargeable) {
				super.onPostExecute(chargeable);
				expenseChargeable.setText(chargeable.getName());
			}
		}.execute();
		chargeNextMonth.setVisibility(expense.isChargedNextMonth() ? View.VISIBLE : View.GONE);
	}

	@UiThread
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		presenter.attachView(this);
		presenter.onActivityResult(requestCode, resultCode);
	}
}
