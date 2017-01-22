package br.com.jonathanzanella.myexpenses.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.user.SelectUserView;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;
import butterknife.OnClick;

import static br.com.jonathanzanella.myexpenses.card.CardType.CREDIT;
import static br.com.jonathanzanella.myexpenses.card.CardType.DEBIT;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditCardActivity extends BaseActivity implements CardContract.EditView {
	public static final String KEY_CARD_UUID = "KeyCardUuid";

	@Bind(R.id.content_view)
	View contentView;
	@Bind(R.id.act_edit_card_name)
	EditText editName;
	@Bind(R.id.act_edit_card_account)
	EditText editAccount;
	@Bind(R.id.act_edit_card_type)
	RadioGroup radioType;
	@Bind(R.id.act_edit_card_user)
	SelectUserView selectUserView;

	private CardPresenter presenter = new CardPresenter(new CardRepository(), new AccountRepository(new Repository<Account>(MyApplication.getContext())));

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		presenter.viewUpdated(false);
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_CARD_UUID))
			presenter.loadCard(extras.getString(KEY_CARD_UUID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		String uuidCard = presenter.getUuid();
		if(uuidCard != null)
			outState.putString(KEY_CARD_UUID, uuidCard);
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

	@OnClick(R.id.act_edit_card_account)
	void onAccount() {
		presenter.showSelectAccountActivity(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		presenter.attachView(this);
		presenter.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onAccountSelected(Account account) {
		editAccount.setText(account.getName());
	}

	@Override
	public void showCard(Card card) {
		editName.setText(card.getName());
		switch (card.getType()) {
			case CREDIT:
				radioType.check(R.id.act_edit_card_type_credit);
				break;
			case DEBIT:
				radioType.check(R.id.act_edit_card_type_debit);
				break;
		}
		selectUserView.setSelectedUser(card.getUserUuid());
	}

	@Override
	public Card fillCard(Card card) {
		card.setName(editName.getText().toString());
		switch (radioType.getCheckedRadioButtonId()) {
			case R.id.act_edit_card_type_credit: {
				card.setType(CREDIT);
				break;
			}
			case R.id.act_edit_card_type_debit: {
				card.setType(DEBIT);
				break;
			}
		}
		card.setUserUuid(selectUserView.getSelectedUser());
		return card;
	}

	@Override
	public void finishView() {
		Intent i = new Intent();
		i.putExtra(KEY_CARD_UUID, presenter.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public void showError(ValidationError error) {
		switch (error) {
			case NAME:
				editName.setError(getString(error.getMessage()));
				break;
			case CARD_TYPE:
				Snackbar.make(contentView, getString(error.getMessage()), Snackbar.LENGTH_SHORT).show();
				break;
			case ACCOUNT:
				editAccount.setError(getString(error.getMessage()));
				break;
			default:
				Log.error(this.getClass().getName(), "Validation unrecognized, field:" + error);
		}
	}
}
