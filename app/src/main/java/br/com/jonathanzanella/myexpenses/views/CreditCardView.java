package br.com.jonathanzanella.myexpenses.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.EditCreditCardActivity;
import br.com.jonathanzanella.myexpenses.adapter.CreditCardAdapter;
import br.com.jonathanzanella.myexpenses.model.CreditCard;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class CreditCardView extends BaseView {
    private static final int REQUEST_ADD_CREDIT_CARD = 1005;
    private CreditCardAdapter adapter;

    @Bind(R.id.view_credit_card_list)
    RecyclerView cards;

    public CreditCardView(Context context) {
        super(context);
    }

    public CreditCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CreditCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_credit_card, this);
        ButterKnife.bind(this);

        adapter = new CreditCardAdapter();
        adapter.loadData();

        cards.setAdapter(adapter);
        cards.setLayoutManager(new GridLayoutManager(getContext(), 1));
        cards.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick(R.id.view_credit_card_fab)
    void onFab() {
        Context ctx = getContext();
        Intent i = new Intent(getContext(), EditCreditCardActivity.class);
        if(ctx instanceof Activity) {
            ((Activity) ctx).startActivityForResult(i, REQUEST_ADD_CREDIT_CARD);
        } else {
            ctx.startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD_CREDIT_CARD:
                if(resultCode == Activity.RESULT_OK) {
                    CreditCard c = CreditCard.find(data.getLongExtra(EditCreditCardActivity.KEY_CREDIT_CARD_ID, 0L));
                    if(c != null)
                        adapter.addCreditCard(c);
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
