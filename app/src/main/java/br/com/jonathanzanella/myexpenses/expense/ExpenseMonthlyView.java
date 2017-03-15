package br.com.jonathanzanella.myexpenses.expense;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressLint("ViewConstructor")
class ExpenseMonthlyView extends BaseView {
    private ExpenseAdapter adapter;

    @Bind(R.id.view_expenses_monthly_list)
    RecyclerView sources;

    private DateTime dateTime;

    public ExpenseMonthlyView(Context context, DateTime dateTime) {
        super(context);
        this.dateTime = dateTime;
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_monthly_expenses, this);
        ButterKnife.bind(this);

        adapter = new ExpenseAdapter();

        sources.setAdapter(adapter);
        sources.setLayoutManager(new GridLayoutManager(getContext(), 1));
        sources.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void refreshData() {
        super.refreshData();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                adapter.loadData(dateTime);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void filter(String s) {
        super.filter(s);
	    adapter.filter(s);
	    adapter.notifyDataSetChanged();
    }
}
