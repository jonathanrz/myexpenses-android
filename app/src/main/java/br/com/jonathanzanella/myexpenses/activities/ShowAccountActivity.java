package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.models.Account;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowAccountActivity extends BaseActivity {
	public static final String KEY_ACCOUNT_ID = "KeyAccountId";

	@Bind(R.id.act_show_account_name)
	TextView accountName;
	@Bind(R.id.act_show_account_balance)
	TextView accountBalance;
	@Bind(R.id.act_show_account_balance_date)
	TextView accountBalanceDate;

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
		}
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_ACCOUNT_ID))
			account = Account.find(extras.getLong(KEY_ACCOUNT_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_ACCOUNT_ID, account.getId());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(account != null) {
			account = Account.find(account.getId());
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
				i.putExtra(EditAccountActivity.KEY_ACCOUNT_ID, account.getId());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
