package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Bill;
import br.com.jonathanzanella.myexpenses.model.Expense;
import br.com.jonathanzanella.myexpenses.model.Receipt;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowExpenseActivity extends BaseActivity {
	public static final String KEY_EXPENSE_ID = "KeyExpenseId";

	@Bind(R.id.act_show_expense_name)
	TextView expenseName;
	@Bind(R.id.act_show_expense_date)
	TextView expenseDate;
	@Bind(R.id.act_show_expense_value)
	TextView expenseIncome;
	@Bind(R.id.act_show_expense_chargeable)
	TextView expenseChargeable;
	@Bind(R.id.act_show_expense_bill)
	TextView expenseBill;
	@Bind(R.id.act_show_expense_charge_next_month)
	TableRow chargeNextMonth;

	private Expense expense;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_expenses);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setData();
	}

	private void setData() {
		expenseName.setText(expense.getName());
		expenseDate.setText(Receipt.sdf.format(expense.getDate().toDate()));
		expenseIncome.setText(NumberFormat.getCurrencyInstance().format(expense.getValue() / 100.0));
		expenseChargeable.setText(expense.getChargeable().getName());
		chargeNextMonth.setVisibility(expense.isChargeNextMonth() ? View.VISIBLE : View.GONE);
		Bill bill = expense.getBill();
		if(bill != null)
			expenseBill.setText(bill.getName());
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_EXPENSE_ID))
			expense = Expense.find(extras.getLong(KEY_EXPENSE_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_EXPENSE_ID, expense.getId());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(expense != null) {
			expense = Expense.find(expense.getId());
			setData();
		}
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
				Intent i = new Intent(this, EditExpenseActivity.class);
				i.putExtra(EditExpenseActivity.KEY_EXPENSE_ID, expense.getId());
				startActivity(i);
				break;
			}
//			case R.id.action_delete: {
//				new AlertDialog.Builder(this)
//						.setTitle(android.R.string.dialog_alert_title)
//						.setMessage(R.string.message_confirm_deletion)
//						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								dialog.dismiss();
//
//								Account a = expense.getAccount();
//								a.credit(expense.getIncome() * -1);
//								a.save();
//								expense.delete();
//								Intent i = new Intent();
//								setResult(RESULT_OK, i);
//								finish();
//							}
//						})
//						.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								dialog.dismiss();
//							}
//						})
//						.show();
//				break;
//			}
		}
		return super.onOptionsItemSelected(item);
	}
}
