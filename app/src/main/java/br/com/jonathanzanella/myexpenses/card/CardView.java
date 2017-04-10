package br.com.jonathanzanella.myexpenses.card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@UiThread
public class CardView extends BaseView {
    private static final int REQUEST_ADD_CARD = 1005;

    @BindView(R.id.view_card_list)
    RecyclerView cards;

	private CardAdapter adapter;

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
        refreshData();

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
                if(resultCode == Activity.RESULT_OK)
                	new AsyncTask<Void, Void, Void>() {

		                @Override
		                protected Void doInBackground(Void... voids) {
			                adapter.loadData();
			                return null;
		                }

		                @Override
		                protected void onPostExecute(Void aVoid) {
			                super.onPostExecute(aVoid);
			                adapter.notifyDataSetChanged();
		                }
	                }.execute();

                break;
        }
    }

    @Override
    public void refreshData() {
        super.refreshData();

	    new AsyncTask<Void, Void, Void>() {

		    @Override
		    protected Void doInBackground(Void... voids) {
			    adapter.loadData();
			    return null;
		    }

		    @Override
		    protected void onPostExecute(Void aVoid) {
			    super.onPostExecute(aVoid);
			    adapter.notifyDataSetChanged();
		    }
	    }.execute();
    }
}
