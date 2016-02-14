package br.com.jonathanzanella.myexpenses.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.adapters.MonthlyPagerAdapter;
import br.com.jonathanzanella.myexpenses.adapters.WeeklyPagerAdapter;
import br.com.jonathanzanella.myexpenses.adapters.WeeklyPagerAdapterBuilder;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 14/02/16.
 */
@SuppressLint("ViewConstructor")
public class OverviewExpensesMonthlyView extends BaseView {
	@Bind(R.id.view_overview_expenses_monthly_tabs)
	TabLayout tabs;
	@Bind(R.id.view_overview_expenses_monthly_pager)
	ViewPager pager;

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

		Log.i("teste", "position=" + adapter.getPositionOfDay(DateTime.now().getDayOfMonth()));
		pager.setCurrentItem(adapter.getPositionOfDay(DateTime.now().getDayOfMonth()));
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_overview_expenses_monthly, this);
		ButterKnife.bind(this);
	}
}