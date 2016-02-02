package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.CreditCard;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowCreditCardActivity extends BaseActivity {
	public static final String KEY_CREDIT_CARD_ID = "KeyCreateCardId";

	@Bind(R.id.act_show_credit_card_name)
	TextView creditCardName;
	@Bind(R.id.act_show_credit_card_account)
	TextView creditCardAccount;
	@Bind(R.id.act_show_credit_card_type)
	TextView creditCardType;

	private CreditCard creditCard;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_credit_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setData();
	}

	private void setData() {
		creditCardName.setText(creditCard.getName());
		creditCardAccount.setText(creditCard.getAccount().getName());
		switch (creditCard.getType()) {
			case CREDIT:
				creditCardType.setText(R.string.credit);
				break;
			case DEBIT:
				creditCardType.setText(R.string.debit);
				break;
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
		outState.putLong(KEY_CREDIT_CARD_ID, creditCard.getId());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(creditCard != null) {
			creditCard = CreditCard.find(creditCard.getId());
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
				Intent i = new Intent(this, EditCreditCardActivity.class);
				i.putExtra(EditCreditCardActivity.KEY_CREDIT_CARD_ID, creditCard.getId());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
