package br.com.jonathanzanella.myexpenses.expense;

import android.annotation.SuppressLint;
import android.content.Context;
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
        adapter.loadData(dateTime);
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

        adapter.loadData(dateTime);
        adapter.notifyDataSetChanged();
    }

	public void addExpense(@NonNull Expense expense) {
		adapter.addExpense(expense);
	}

    @Override
    public void filter(String s) {
        super.filter(s);
	    adapter.filter(s);
	    adapter.notifyDataSetChanged();
    }
}
