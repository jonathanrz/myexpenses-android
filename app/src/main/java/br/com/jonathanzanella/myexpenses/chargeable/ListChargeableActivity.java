package br.com.jonathanzanella.myexpenses.chargeable;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.AccountAdapter;
import br.com.jonathanzanella.myexpenses.account.AccountAdapterCallback;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import br.com.jonathanzanella.myexpenses.card.CardAdapter;
import br.com.jonathanzanella.myexpenses.card.CardAdapterCallback;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.card.Card;
import butterknife.Bind;

/**
 * Created by jzanella onCard 2/1/16.
 */
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
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		initAccounts();
		initCreditCards();
	}

	@NonNull
	private void initAccounts() {
		AccountAdapter adapter = new AccountAdapter();
		adapter.setCallback(this);
		adapter.setSimplified(true);
		adapter.loadData();

		accounts.setAdapter(adapter);
		accounts.setHasFixedSize(true);
		accounts.setLayoutManager(new GridLayoutManager(this, 2));
		accounts.setItemAnimator(new DefaultItemAnimator());
	}

	private void initCreditCards() {
		CardAdapter adapter = new CardAdapter();
		adapter.setCallback(this);
		adapter.loadData();

		cards.setAdapter(adapter);
		cards.setHasFixedSize(true);
		cards.setLayoutManager(new GridLayoutManager(this, 2));
		cards.setItemAnimator(new DefaultItemAnimator());
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