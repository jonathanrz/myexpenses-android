package br.com.jonathanzanella.myexpenses.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.activities.EditCardActivity;
import br.com.jonathanzanella.myexpenses.adapters.CardAdapter;
import br.com.jonathanzanella.myexpenses.models.Card;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class CardView extends BaseView {
    private static final int REQUEST_ADD_CARD = 1005;
    private CardAdapter adapter;

    @Bind(R.id.view_card_list)
    RecyclerView cards;

    public CardView(Context context) {
        super(context);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_card, this);
        ButterKnife.bind(this);

        adapter = new CardAdapter();
        adapter.loadData();

        cards.setAdapter(adapter);
        cards.setLayoutManager(new GridLayoutManager(getContext(), 1));
        cards.setItemAnimator(new DefaultItemAnimator());
    }

    @OnClick(R.id.view_card_fab)
    void onFab() {
        Context ctx = getContext();
        Intent i = new Intent(getContext(), EditCardActivity.class);
        if(ctx instanceof Activity) {
            ((Activity) ctx).startActivityForResult(i, REQUEST_ADD_CARD);
        } else {
            ctx.startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD_CARD:
                if(resultCode == Activity.RESULT_OK) {
                    Card c = Card.find(data.getLongExtra(EditCardActivity.KEY_CARD_ID, 0L));
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
