package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditAccountActivity extends BaseActivity {
	public static final String KEY_ACCOUNT_UUID = "KeyAccountUuid";

	@Bind(R.id.act_edit_account_name)
	EditText editName;
	@Bind(R.id.act_edit_account_balance)
	EditText editBalance;
	@Bind(R.id.act_edit_account_to_pay_credit_card)
	CheckBox checkToPayCreditCard;
	@Bind(R.id.act_edit_account_to_pay_bills)
	CheckBox checkToPayBill;
	@Bind(R.id.act_edit_account_user)
	SelectUserView selectUserView;

	private Account account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_account);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		editBalance.addTextChangedListener(new CurrencyTextWatch(editBalance));

		if(account != null) {
			editName.setText(account.getName());
			editBalance.setText(NumberFormat.getCurrencyInstance().format(account.getBalance() / 100.0));
			checkToPayCreditCard.setChecked(account.isAccountToPayCreditCard());
			checkToPayBill.setChecked(account.isAccountToPayBills());
			selectUserView.setSelectedUser(account.getUserUuid());
		}
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
		if(account != null)
			outState.putString(KEY_ACCOUNT_UUID, account.getUuid());
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

	private void save() {
		if(account == null)
			account = new Account();
		account.setName(editName.getText().toString());
		account.setBalance(Integer.parseInt(editBalance.getText().toString().replaceAll("[^\\d]", "")));
		account.setAccountToPayCreditCard(checkToPayCreditCard.isChecked());
		account.setAccountToPayBills(checkToPayBill.isChecked());
		account.setUserUuid(selectUserView.getSelectedUser());
		account.save();

		Intent i = new Intent();
		i.putExtra(KEY_ACCOUNT_UUID, account.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
