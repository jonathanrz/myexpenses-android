package br.com.jonathanzanella.myexpenses.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

/**
 * Created by jzanella on 2/1/16.
 */
public class ListBillActivity extends BaseActivity implements BillAdapterCallback {
	public static final String KEY_BILL_SELECTED_UUID = "KeyBillSelectUuid";

	@Bind(R.id.act_bill_list)
	RecyclerView bills;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_bill);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		BillAdapter adapter = new BillAdapter();
		adapter.setCallback(this);

		bills.setAdapter(adapter);
		bills.setHasFixedSize(true);
		bills.setLayoutManager(new GridLayoutManager(this, 2));
		bills.setItemAnimator(new DefaultItemAnimator());
	}

	@Override
	public void onBillSelected(Bill bill) {
		Intent i = new Intent();
		i.putExtra(KEY_BILL_SELECTED_UUID, bill.getUuid());
		setResult(RESULT_OK, i);
		finish();
	}
}
