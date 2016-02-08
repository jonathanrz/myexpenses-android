package br.com.jonathanzanella.myexpenses.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapter.ReceiptAdapter;
import br.com.jonathanzanella.myexpenses.model.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
@SuppressLint("ViewConstructor")
public class ReceiptMonthlyView extends BaseView {
    private ReceiptAdapter adapter;

    @Bind(R.id.view_receipts_monthly_list)
    RecyclerView sources;

    private DateTime dateTime;

    public ReceiptMonthlyView(Context context, DateTime dateTime) {
        super(context);
        this.dateTime = dateTime;

        adapter.loadData(dateTime);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_receipts_monthly, this);
        ButterKnife.bind(this);

        adapter = new ReceiptAdapter();

        sources.setAdapter(adapter);
        sources.setLayoutManager(new GridLayoutManager(getContext(), 1));
        sources.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void refreshData() {
        super.refreshData();

        adapter.loadData(dateTime);
        adapter.notifyDataSetChanged();
    }

	public void addReceipt(@NonNull Receipt receipt) {
		adapter.addReceipt(receipt);
	}
}
