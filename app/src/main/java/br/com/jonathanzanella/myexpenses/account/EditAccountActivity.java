package br.com.jonathanzanella.myexpenses.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyTextWatch;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.BindView;

public class EditAccountActivity extends BaseActivity implements AccountContract.EditView {
	public static final String KEY_ACCOUNT_UUID = "KeyAccountUuid";

	@BindView(R.id.act_edit_account_name)
	EditText editName;
	@BindView(R.id.act_edit_account_balance)
	EditText editBalance;
	@BindView(R.id.act_edit_account_negative)
	CheckBox checkAccountBalanceNegative;
	@BindView(R.id.act_edit_account_to_pay_credit_card)
	CheckBox checkToPayCreditCard;
	@BindView(R.id.act_edit_account_to_pay_bills)
	CheckBox checkToPayBill;
	@BindView(R.id.act_edit_account_show_in_resume)
	CheckBox checkShowInResume;

	private final AccountPresenter presenter;

	public EditAccountActivity() {
		presenter = new AccountPresenter(new AccountRepository(new RepositoryImpl<Account>(MyApplication.getContext())));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_account);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		editBalance.addTextChangedListener(new CurrencyTextWatch(editBalance));
		presenter.viewUpdated(false);
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
		account.setShowInResume(checkShowInResume.isChecked());
		return account;
	}

	@Override
	public void showAccount(Account account) {
		editName.setText(account.getName());
		int balance = account.getBalance();
		if(balance > 0) {
			editBalance.setText(CurrencyHelper.format(balance));
			checkAccountBalanceNegative.setChecked(false);
		} else {
			editBalance.setText(CurrencyHelper.format(balance * -1));
			checkAccountBalanceNegative.setChecked(true);
		}
		checkToPayCreditCard.setChecked(account.isAccountToPayCreditCard());
		checkToPayBill.setChecked(account.isAccountToPayBills());
		checkShowInResume.setChecked(account.showInResume());
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
