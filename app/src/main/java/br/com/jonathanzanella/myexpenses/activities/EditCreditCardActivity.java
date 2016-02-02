package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Account;
import br.com.jonathanzanella.myexpenses.model.CreditCard;
import br.com.jonathanzanella.myexpenses.model.CreditCardType;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditCreditCardActivity extends BaseActivity {
	public static final String KEY_CREDIT_CARD_ID = "KeyCreditCardId";
	private static final int REQUEST_SELECT_ACCOUNT = 1006;

	@Bind(R.id.act_edit_credit_card_name)
	EditText editName;
	@Bind(R.id.act_edit_credit_card_account)
	EditText editAccount;
	@Bind(R.id.act_edit_credit_card_type)
	RadioGroup radioType;

	private CreditCard creditCard;
	private Account account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_credit_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if(creditCard != null) {
			editName.setText(creditCard.getName());
			account = creditCard.getAccount();
			onAccountSelected();
			switch (creditCard.getType()) {
				case CREDIT:
					radioType.check(R.id.act_edit_credit_card_type_credit);
					break;
				case DEBIT:
					radioType.check(R.id.act_edit_credit_card_type_debit);
					break;
			}
		}
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras == null)
			return;

		if(extras.containsKey(KEY_CREDIT_CARD_ID))
			creditCard = CreditCard.find(extras.getLong(KEY_CREDIT_CARD_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(creditCard != null)
			outState.putLong(KEY_CREDIT_CARD_ID, creditCard.getId());
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

	@OnClick(R.id.act_edit_credit_card_account)
	void onAccount() {
		if(creditCard == null)
			startActivityForResult(new Intent(this, ListAccountActivity.class), REQUEST_SELECT_ACCOUNT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_SELECT_ACCOUNT: {
				if(resultCode == RESULT_OK) {
					account = Account.find(data.getLongExtra(ListAccountActivity.KEY_ACCOUNT_SELECTED_ID, 0L));
					if(account != null)
						onAccountSelected();
				}
				break;
			}
		}
	}

	private void onAccountSelected() {
		editAccount.setText(account.getName());
	}

	private void save() {
		if(creditCard == null)
			creditCard = new CreditCard();
		creditCard.setName(editName.getText().toString());
		creditCard.setAccount(account);
		switch (radioType.getCheckedRadioButtonId()) {
			case R.id.act_edit_credit_card_type_credit: {
				creditCard.setType(CreditCardType.CREDIT);
				break;
			}
			case R.id.act_edit_credit_card_type_debit: {
				creditCard.setType(CreditCardType.DEBIT);
				break;
			}
		}
		creditCard.save();

		Intent i = new Intent();
		i.putExtra(KEY_CREDIT_CARD_ID, creditCard.getId());
		setResult(RESULT_OK, i);
		finish();
	}
}
