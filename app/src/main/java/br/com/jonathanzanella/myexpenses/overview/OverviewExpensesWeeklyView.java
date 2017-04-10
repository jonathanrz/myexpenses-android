package br.com.jonathanzanella.myexpenses.overview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.expense.ExpenseWeeklyOverviewAdapter;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("ViewConstructor")
class OverviewExpensesWeeklyView extends BaseView {
	@BindView(R.id.view_overview_expenses_weekly_list)
	RecyclerView list;
	@BindView(R.id.view_overview_expenses_weekly_total)
	TextView total;

	private final WeeklyPagerAdapter.Period period;
	private final ExpenseRepository expenseRepository;

	private ExpenseWeeklyOverviewAdapter adapter;

	public OverviewExpensesWeeklyView(Context context, WeeklyPagerAdapter.Period period) {
		super(context);
		this.period = period;
		expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(context));
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_overview_expenses_weekly, this);
		ButterKnife.bind(this);

		adapter = new ExpenseWeeklyOverviewAdapter();

		list.setAdapter(adapter);
		list.setHasFixedSize(true);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
	}

	@Override
	public void refreshData() {
		super.refreshData();

		adapter.setExpenses(expenseRepository.expenses(period));
		adapter.notifyDataSetChanged();

		total.setText(CurrencyHelper.format(adapter.getTotalValue()));
	}
}