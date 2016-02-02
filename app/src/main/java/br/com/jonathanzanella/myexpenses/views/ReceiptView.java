package br.com.jonathanzanella.myexpenses.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.EditReceiptActivity;
import br.com.jonathanzanella.myexpenses.adapter.ReceiptAdapter;
import br.com.jonathanzanella.myexpenses.model.Receipt;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class ReceiptView extends BaseView {
    private static final int REQUEST_ADD_RECEIPT = 1005;
    private ReceiptAdapter adapter;

    @Bind(R.id.view_receipts_list)
    RecyclerView sources;

    public ReceiptView(Context context) {
        super(context);
    }

    public ReceiptView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReceiptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_receipts, this);
        ButterKnife.bind(this);

        adapter = new ReceiptAdapter();
        adapter.loadData();

        sources.setAdapter(adapter);
        sources.setLayoutManager(new GridLayoutManager(getContext(), 1));
        sources.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick(R.id.view_receipts_fab)
    void onFab() {
        Context ctx = getContext();
        Intent i = new Intent(getContext(), EditReceiptActivity.class);
        if(ctx instanceof Activity) {
            ((Activity) ctx).startActivityForResult(i, REQUEST_ADD_RECEIPT);
        } else {
            ctx.startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD_RECEIPT:
                if(resultCode == Activity.RESULT_OK) {
                    Receipt r = Receipt.find(data.getLongExtra(EditReceiptActivity.KEY_RECEIPT_ID, 0L));
                    if(r != null)
                        adapter.addReceipt(r);
                }
                break;
        }
    }

    @Override
    public void refreshData() {
        super.refreshData();

        adapter.loadData();
        adapter.notifyDataSetChanged();
    }
}
