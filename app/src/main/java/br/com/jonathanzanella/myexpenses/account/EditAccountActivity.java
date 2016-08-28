package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

import static java.text.NumberFormat.getCurrencyInstance;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditAccountActivity extends BaseActivity implements AccountContract.EditView {
	public static final String KEY_ACCOUNT_UUID = "KeyAccountUuid";

	@Bind(R.id.act_edit_account_name)
	EditText editName;
	@Bind(R.id.act_edit_account_balance)
	EditText editBalance;
	@Bind(R.id.act_edit_account_negative)
	CheckBox checkAccountBalanceNegative;
	@Bind(R.id.act_edit_account_to_pay_credit_card)
	CheckBox checkToPayCreditCard;
	@Bind(R.id.act_edit_account_to_pay_bills)
	CheckBox checkToPayBill;
	@Bind(R.id.act_edit_account_user)
	SelectUserView selectUserView;

	private AccountPresenter presenter = new AccountPresenter(new AccountRepository());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_account);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		editBalance.addTextChangedListener(new CurrencyTextWatch(editBalance));
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_ACCOUNT_UUID))
			presenter.loadAccount(extras.getString(KEY_ACCOUNT_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String uuid = presenter.getUuid();
		if(uuid != null)
			outState.putString(KEY_ACCOUNT_UUID, uuid);
	}

	@Override
	protected void onStart() {
		super.onStart();
		presenter.attachView(this);
		presenter.viewUpdated(false);
	}

	@Override
	protected void onStop() {
		presenter.detachView();
		super.onStop();
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

	@Override
	public Account fillAccount(Account account) {
		account.setName(editName.getText().toString());
		String balanceText = editBalance.getText().toString().replaceAll("[^\\d]", "");
		int balance = StringUtils.isEmpty(balanceText) ? 0 : Integer.parseInt(balanceText);
		if(checkAccountBalanceNegative.isChecked())
			account.setBalance(balance * -1);
		else
			account.setBalance(balance);
		account.setAccountToPayCreditCard(checkToPayCreditCard.isChecked());
		account.setAccountToPayBills(checkToPayBill.isChecked());
		account.setUserUuid(selectUserView.getSelectedUser());
		return account;
	}

	@Override
	public void showAccount(Account account) {
		editName.setText(account.getName());
		int balance = account.getBalance();
		if(balance > 0) {
			editBalance.setText(getCurrencyInstance().format(balance / 100.0));
			checkAccountBalanceNegative.setChecked(false);
		} else {
			editBalance.setText(getCurrencyInstance().format(balance * -1 / 100.0));
			checkAccountBalanceNegative.setChecked(true);
		}
		checkToPayCreditCard.setChecked(account.isAccountToPayCreditCard());
		checkToPayBill.setChecked(account.isAccountToPayBills());
		selectUserView.setSelectedUser(account.getUserUuid());
	}

	@Override
	public void showError(ValidationError error) {
		switch (error) {
			case NAME:
				editName.setError(getString(error.getMessage()));
				break;
			default:
				Log.error(this.getClass().getName(), "Validation unrecognized, field:" + error);
		}
	}

	@Override
	public void finishView() {
		Intent i = new Intent();
		i.putExtra(KEY_ACCOUNT_UUID, presenter.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
