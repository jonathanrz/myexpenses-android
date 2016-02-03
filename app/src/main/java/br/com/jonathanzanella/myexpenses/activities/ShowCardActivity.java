package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Card;
import butterknife.Bind;

/**
 * Created by jzanella on 1/31/16.
 */
public class ShowCardActivity extends BaseActivity {
	public static final String KEY_CREDIT_CARD_ID = "KeyCreateCardId";

	@Bind(R.id.act_show_card_name)
	TextView cardName;
	@Bind(R.id.act_show_card_account)
	TextView cardAccount;
	@Bind(R.id.act_show_card_type)
	TextView cardType;

	private Card card;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_card);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setData();
	}

	private void setData() {
		cardName.setText(card.getName());
		cardAccount.setText(card.getAccount().getName());
		switch (card.getType()) {
			case CREDIT:
				cardType.setText(R.string.credit);
				break;
			case DEBIT:
				cardType.setText(R.string.debit);
				break;
		}
	}

	@Override
	protected void storeBundle(Bundle extras) {
		super.storeBundle(extras);
		if(extras == null)
			return;
		if(extras.containsKey(KEY_CREDIT_CARD_ID))
			card = Card.find(extras.getLong(KEY_CREDIT_CARD_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(KEY_CREDIT_CARD_ID, card.getId());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(card != null) {
			card = Card.find(card.getId());
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
				Intent i = new Intent(this, EditCardActivity.class);
				i.putExtra(EditCardActivity.KEY_CARD_ID, card.getId());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
