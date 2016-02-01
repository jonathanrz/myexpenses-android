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

	private CreditCard creditCard;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_credit_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		creditCardName.setText(creditCard.getName());
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
