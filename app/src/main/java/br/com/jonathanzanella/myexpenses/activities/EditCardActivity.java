package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.models.Account;
import br.com.jonathanzanella.myexpenses.models.Card;
import br.com.jonathanzanella.myexpenses.models.CardType;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditCardActivity extends BaseActivity {
	public static final String KEY_CARD_UUID = "KeyCardUuid";
	private static final int REQUEST_SELECT_ACCOUNT = 1006;

	@Bind(R.id.act_edit_card_name)
	EditText editName;
	@Bind(R.id.act_edit_card_account)
	EditText editAccount;
	@Bind(R.id.act_edit_card_type)
	RadioGroup radioType;

	private Card card;
	private Account account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if(card != null) {
			editName.setText(card.getName());
			account = card.getAccount();
			onAccountSelected();
			switch (card.getType()) {
				case CREDIT:
					radioType.check(R.id.act_edit_card_type_credit);
					break;
				case DEBIT:
					radioType.check(R.id.act_edit_card_type_debit);
					break;
			}
		}
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras == null)
			return;

		if(extras.containsKey(KEY_CARD_UUID))
			card = Card.find(extras.getString(KEY_CARD_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(card != null)
			outState.putString(KEY_CARD_UUID, card.getUuid());
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

	@OnClick(R.id.act_edit_card_account)
	void onAccount() {
		if(card == null)
			startActivityForResult(new Intent(this, ListAccountActivity.class), REQUEST_SELECT_ACCOUNT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_SELECT_ACCOUNT: {
				if(resultCode == RESULT_OK) {
					account = Account.find(data.getStringExtra(ListAccountActivity.KEY_ACCOUNT_SELECTED_UUID));
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
		if(card == null)
			card = new Card();
		card.setName(editName.getText().toString());
		card.setAccount(account);
		switch (radioType.getCheckedRadioButtonId()) {
			case R.id.act_edit_card_type_credit: {
				card.setType(CardType.CREDIT);
				break;
			}
			case R.id.act_edit_card_type_debit: {
				card.setType(CardType.DEBIT);
				break;
			}
		}
		card.save();

		Intent i = new Intent();
		i.putExtra(KEY_CARD_UUID, card.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
