package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.CreditCard;
import butterknife.Bind;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class EditCreditCardActivity extends BaseActivity {
	public static final String KEY_CREDIT_CARD_ID = "KeyCreditCardId";

	@Bind(R.id.act_edit_credit_card_name)
	EditText editName;

	private CreditCard creditCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_credit_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if(creditCard != null)
			editName.setText(creditCard.getName());
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

	private void save() {
		if(creditCard == null)
			creditCard = new CreditCard();
		creditCard.setName(editName.getText().toString());
		creditCard.save();

		Intent i = new Intent();
		i.putExtra(KEY_CREDIT_CARD_ID, creditCard.getId());
		setResult(RESULT_OK, i);
		finish();
	}
}
