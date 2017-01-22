package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.bill.BillRepository;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.Subscriber;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowExpenseActivity extends BaseActivity implements ExpenseContract.View {
	public static final String KEY_EXPENSE_UUID = ExpensePresenter.KEY_EXPENSE_UUID;

	@Bind(R.id.act_show_expense_name)
	TextView expenseName;
	@Bind(R.id.act_show_expense_date)
	TextView expenseDate;
	@Bind(R.id.act_show_expense_value)
	TextView expenseIncome;
	@Bind(R.id.act_show_expense_value_to_show_in_overview)
	TextView expenseIncomeToShowInOverview;
	@Bind(R.id.act_show_expense_chargeable)
	TextView expenseChargeable;
	@Bind(R.id.act_show_expense_bill)
	TextView expenseBill;
	@Bind(R.id.act_show_expense_charge_next_month)
	TableRow chargeNextMonth;

	private ExpensePresenter presenter = new ExpensePresenter(new ExpenseRepository(), new BillRepository(new Repository<Bill>(MyApplication.getContext())));

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_expenses);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
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

	@Override
	public void showExpense(Expense expense) {
		expenseName.setText(expense.getName());
		expenseDate.setText(Receipt.sdf.format(expense.getDate().toDate()));
		expenseIncome.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
		expenseIncomeToShowInOverview.setText(NumberFormat.getCurrencyInstance().format(expense.getValueToShowInOverview() / 100.0));
		expense.getChargeable()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Chargeable>("ShowExpenseActivity.showExpense") {

					@Override
					public void onNext(Chargeable chargeable) {
						expenseChargeable.setText(chargeable.getName());
					}
				});
		chargeNextMonth.setVisibility(expense.isChargeNextMonth() ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		presenter.attachView(this);
		presenter.onActivityResult(requestCode, resultCode, data);
	}
}
