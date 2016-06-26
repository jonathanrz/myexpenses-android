package br.com.jonathanzanella.myexpenses.overview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.NumberFormat;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 14/02/16.
 */
@SuppressLint("ViewConstructor")
class OverviewExpensesMonthlyView extends BaseView {
	@Bind(R.id.view_overview_expenses_monthly_tabs)
	TabLayout tabs;
	@Bind(R.id.view_overview_expenses_monthly_pager)
	ViewPager pager;
	@Bind(R.id.view_overview_expenses_monthly_total)
	TextView monthlyTotal;

	public OverviewExpensesMonthlyView(Context context, DateTime month) {
		super(context);

		WeeklyPagerAdapter adapter = new WeeklyPagerAdapter(getContext(), month, new WeeklyPagerAdapterBuilder() {

			@Override
			public BaseView buildView(Context ctx, WeeklyPagerAdapter.Period period) {
				return new OverviewExpensesWeeklyView(ctx, period);
			}
		});

		pager.setAdapter(adapter);
		pager.setCurrentItem(MonthlyPagerAdapter.INIT_MONTH_VISIBLE);
		tabs.setupWithViewPager(pager);

		DateTime now = DateTime.now();
		if(month.getMonthOfYear() == now.getMonthOfYear() && month.getYear() == now.getYear())
			pager.setCurrentItem(adapter.getPositionOfDay(now.getDayOfMonth()));

		WeeklyPagerAdapter.Period period = new WeeklyPagerAdapter.Period();
		period.init = month.dayOfMonth().withMinimumValue();
		period.end = month.dayOfMonth().withMaximumValue();
		int total = 0;
		for (Expense expense : Expense.expenses(period))
			total += expense.getValue();

		monthlyTotal.setText(NumberFormat.getCurrencyInstance().format(total / 100.0));
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_overview_expenses_monthly, this);
		ButterKnife.bind(this);
	}
}