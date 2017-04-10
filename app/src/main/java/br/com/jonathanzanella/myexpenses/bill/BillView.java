package br.com.jonathanzanella.myexpenses.bill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillView extends BaseView {
	private static final int REQUEST_ADD_BILL = 1003;

	@BindView(R.id.view_bills_list)
	RecyclerView bills;
	
	private BillAdapter adapter;

	public BillView(Context context) {
		super(context);
	}

	public BillView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BillView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_bills, this);
		ButterKnife.bind(this);

		adapter = new BillAdapter();

		bills.setAdapter(adapter);
		bills.setLayoutManager(new GridLayoutManager(getContext(), 2));
		bills.setItemAnimator(new DefaultItemAnimator());
	}

	@OnClick(R.id.view_bills_fab)
	void onFab() {
		Context ctx = getContext();
		Intent i = new Intent(getContext(), EditBillActivity.class);
		if(ctx instanceof Activity) {
			((Activity) ctx).startActivityForResult(i, REQUEST_ADD_BILL);
		} else {
			ctx.startActivity(i);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REQUEST_ADD_BILL:
				if(resultCode == Activity.RESULT_OK)
					refreshData();
				break;
		}
	}

	@Override
	public void refreshData() {
		super.refreshData();

		adapter.refreshData();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void filter(String s) {
		super.filter(s);
		adapter.filter(s);
		adapter.notifyDataSetChanged();
	}
}
