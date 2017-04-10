package br.com.jonathanzanella.myexpenses.chargeable;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountAdapter;
import br.com.jonathanzanella.myexpenses.account.AccountAdapterCallback;
import br.com.jonathanzanella.myexpenses.card.Card;
import br.com.jonathanzanella.myexpenses.card.CardAdapter;
import br.com.jonathanzanella.myexpenses.card.CardAdapterCallback;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

public class ListChargeableActivity extends BaseActivity implements AccountAdapterCallback, CardAdapterCallback {
	public static final String KEY_CHARGEABLE_SELECTED_UUID = "KeyChargeableSelectUuid";
	public static final String KEY_CHARGEABLE_SELECTED_TYPE = "KeyChargeableSelectType";

	@Bind(R.id.act_chargeable_list_accounts)
	RecyclerView accounts;
	@Bind(R.id.act_chargeable_list_cards)
	RecyclerView cards;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_chargeable);
		setTitle(R.string.select_chargeable_title);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		initAccounts();
		initCreditCards();
	}

	private void initAccounts() {
		AccountAdapter adapter = new AccountAdapter();
		adapter.setCallback(this);
		adapter.setFormat(AccountAdapter.Format.LIST);

		accounts.setAdapter(adapter);
		accounts.setHasFixedSize(true);
		accounts.setLayoutManager(new GridLayoutManager(this, 2));
		accounts.setItemAnimator(new DefaultItemAnimator());
	}

	private void initCreditCards() {
		final CardAdapter adapter = new CardAdapter();
		adapter.setCallback(this);
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {
				adapter.loadData();
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				cards.setAdapter(adapter);
				cards.setHasFixedSize(true);
				cards.setLayoutManager(new GridLayoutManager(ListChargeableActivity.this, 2));
				cards.setItemAnimator(new DefaultItemAnimator());
			}
		}.execute();
	}

	@Override
	public void onAccountSelected(Account account) {
		Intent i = new Intent();
		i.putExtra(KEY_CHARGEABLE_SELECTED_UUID, account.getUuid());
		i.putExtra(KEY_CHARGEABLE_SELECTED_TYPE, account.getChargeableType());
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public void onCard(Card card) {
		Intent i = new Intent();
		i.putExtra(KEY_CHARGEABLE_SELECTED_UUID, card.getUuid());
		i.putExtra(KEY_CHARGEABLE_SELECTED_TYPE, card.getChargeableType());
		setResult(RESULT_OK, i);
		finish();
	}
}