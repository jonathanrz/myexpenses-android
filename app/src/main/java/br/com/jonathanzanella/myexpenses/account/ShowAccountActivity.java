package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowAccountActivity extends BaseActivity implements AccountContract.View {
	public static final String KEY_ACCOUNT_UUID = "KeyAccountUuid";
	public static final String KEY_ACCOUNT_MONTH_TO_SHOW = "KeyAccountMonthToShow";

	@Bind(R.id.act_show_account_name)
	TextView accountName;
	@Bind(R.id.act_show_account_balance)
	TextView accountBalance;
	@Bind(R.id.act_show_account_to_pay_credit_card)
	TextView accountToPayCreditCard;
	@Bind(R.id.act_show_account_to_pay_bills)
	TextView accountToPayBills;
	@Bind(R.id.act_show_account_transactions)
	TransactionsView transactionsView;

	private AccountPresenter presenter = new AccountPresenter(new AccountRepository());
	private DateTime monthToShow;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_account);

		if(monthToShow == null)
			monthToShow = DateHelper.firstDayOfMonth(DateTime.now());
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		presenter.loadAccount(extras.getString(KEY_ACCOUNT_UUID));
		if(extras.containsKey(KEY_ACCOUNT_MONTH_TO_SHOW))
			monthToShow = DateHelper.firstDayOfMonth(new DateTime(extras.getLong(KEY_ACCOUNT_MONTH_TO_SHOW)));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		presenter.viewUpdated(false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_ACCOUNT_UUID, presenter.getUuid());
		outState.putLong(KEY_ACCOUNT_MONTH_TO_SHOW, monthToShow.getMillis());
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
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				Intent i = new Intent(this, EditAccountActivity.class);
				i.putExtra(EditAccountActivity.KEY_ACCOUNT_UUID, presenter.getUuid());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void showAccount(Account account) {
		accountName.setText(account.getName());
		accountBalance.setText(NumberFormat.getCurrencyInstance().format(account.getBalance() / 100.0));
		accountToPayCreditCard.setText(account.isAccountToPayCreditCard() ? R.string.yes : R.string.no);
		accountToPayBills.setText(account.isAccountToPayBills() ? R.string.yes : R.string.no);

		transactionsView.showTransactions(account, monthToShow);
	}
}
