package br.com.jonathanzanella.myexpenses.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapters.ExpenseMonthlyResumeAdapter;
import br.com.jonathanzanella.myexpenses.adapters.WeeklyPagerAdapter;
import br.com.jonathanzanella.myexpenses.models.Expense;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 14/02/16.
 */
@SuppressLint("ViewConstructor")
public class OverviewExpensesWeeklyView extends BaseView {
	@Bind(R.id.view_overview_expenses_weekly_list)
	RecyclerView list;

	private ExpenseMonthlyResumeAdapter adapter;
	private WeeklyPagerAdapter.Period period;

	public OverviewExpensesWeeklyView(Context context, WeeklyPagerAdapter.Period period) {
		super(context);
		this.period = period;
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_overview_expenses_weekly, this);
		ButterKnife.bind(this);

		adapter = new ExpenseMonthlyResumeAdapter();

		list.setAdapter(adapter);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	@Override
	public void refreshData() {
		super.refreshData();

		adapter.setExpenses(Expense.expenses(period));
		adapter.notifyDataSetChanged();
	}
}
