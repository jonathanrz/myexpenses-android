package br.com.jonathanzanella.myexpenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapter.AccountAdapter;
import br.com.jonathanzanella.myexpenses.adapter.AccountAdapterCallback;
import br.com.jonathanzanella.myexpenses.model.Account;
import butterknife.Bind;

/**
 * Created by jzanella on 2/1/16.
 */
public class ListChargeableActivity extends BaseActivity implements AccountAdapterCallback {
	public static final String KEY_CHARGEABLE_SELECTED_ID = "KeyChargeableSelectId";
	public static final String KEY_CHARGEABLE_SELECTED_TYPE = "KeyChargeableSelectType";

	@Bind(R.id.act_chargeable_list_accounts)
	RecyclerView accounts;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_chargeable);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		AccountAdapter adapter = new AccountAdapter();
		adapter.setCallback(this);
		adapter.loadData();

		accounts.setAdapter(adapter);
		accounts.setHasFixedSize(true);
		accounts.setLayoutManager(new GridLayoutManager(this, 2));
		accounts.setItemAnimator(new DefaultItemAnimator());
	}

	@Override
	public void onAccountSelected(Account account) {
		Intent i = new Intent();
		i.putExtra(KEY_CHARGEABLE_SELECTED_ID, account.getId());
		i.putExtra(KEY_CHARGEABLE_SELECTED_TYPE, account.getClass().getName());
		setResult(RESULT_OK, i);
		finish();
	}
}