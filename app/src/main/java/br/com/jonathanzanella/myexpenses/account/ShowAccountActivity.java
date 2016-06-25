package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import br.com.jonathanzanella.myexpenses.transaction.TransactionAdapter;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.receipt.Receipt;
import br.com.jonathanzanella.myexpenses.transaction.Transaction;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
class ShowAccountActivity extends BaseActivity {
	public static final String KEY_ACCOUNT_UUID = "KeyAccountUuid";

	@Bind(R.id.act_show_account_name)
	TextView accountName;
	@Bind(R.id.act_show_account_balance)
	TextView accountBalance;
	@Bind(R.id.act_show_account_balance_date)
	TextView accountBalanceDate;
	@Bind(R.id.act_show_account_transactions)
	RecyclerView transactions;
	@Bind(R.id.act_show_account_month_balance)
	TextView monthBalance;
	@Bind(R.id.act_show_account_next_month_transactions)
	RecyclerView nextMonthTransactions;
	@Bind(R.id.act_show_account_next_month_balance)
	TextView nextMonthBalance;
	@Bind(R.id.act_show_account_to_pay_credit_card)
	TextView accountToPayCreditCard;

	private Account account;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_account);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setData();
	}

	private void setData() {
		if (account != null) {
			accountName.setText(account.getName());
			accountBalance.setText(NumberFormat.getCurrencyInstance().format(account.getBalance() / 100.0));
			accountBalanceDate.setText(Account.sdf.format(account.getBalanceDate().toDate()));
			accountToPayCreditCard.setText(account.isAccountToPayCreditCard() ? R.string.yes : R.string.no);

			DateTime month = DateTime.now().withDayOfMonth(1);
			int nextMonthBalance = showBalance(month, account.getBalance(), transactions, monthBalance);
			showBalance(month.plusMonths(1), nextMonthBalance, nextMonthTransactions, this.nextMonthBalance);
		}
	}

	private int showBalance(DateTime month, int balance, RecyclerView list, TextView balanceView) {
		TransactionAdapter adapter = new TransactionAdapter();
		adapter.addTransactions(Receipt.monthly(month, account));
		List<Expense> expenses = Expense.accountExpenses(account, month);
		adapter.addTransactions(expenses);
		adapter.addTransactions(Bill.monthly(month, expenses));
		adapter.notifyDataSetChanged();

		list.setAdapter(adapter);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(this));
		list.setNestedScrollingEnabled(false);

		for (Transaction transaction : adapter.getTransactions()) {
			if(!transaction.credited()) {
				balance += transaction.getAmount();
			} else if(!transaction.debited()) {
				balance -= transaction.getAmount();
			}
		}

		balanceView.setText(NumberFormat.getCurrencyInstance().format(balance / 100.0));
		//noinspection deprecation
		balanceView.setTextColor(getResources().getColor(balance >= 0 ? R.color.value_unreceived : R.color.value_unpaid));

		return balance;
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_ACCOUNT_UUID))
			account = Account.find(extras.getString(KEY_ACCOUNT_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_ACCOUNT_UUID, account.getUuid());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(account != null) {
			account = Account.find(account.getUuid());
			setData();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				Intent i = new Intent(this, EditAccountActivity.class);
				i.putExtra(EditAccountActivity.KEY_ACCOUNT_UUID, account.getUuid());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
