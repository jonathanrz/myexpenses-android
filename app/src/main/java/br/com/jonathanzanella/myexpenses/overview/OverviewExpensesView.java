package br.com.jonathanzanella.myexpenses.overview;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import org.joda.time.DateTime;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter;
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class OverviewExpensesView extends BaseView {
	@Bind(R.id.view_overview_expenses_pager)
	ViewPager pager;

	public OverviewExpensesView(Context context) {
		super(context);
	}

	public OverviewExpensesView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OverviewExpensesView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_overview_expenses, this);
		ButterKnife.bind(this);

		MonthlyPagerAdapter adapter = new MonthlyPagerAdapter(getContext(), new MonthlyPagerAdapterBuilder() {
			@Override
			public BaseView buildView(Context ctx, DateTime date) {
				return new OverviewExpensesMonthlyView(ctx, date);
			}
		});
		pager.setAdapter(adapter);
		pager.setCurrentItem(MonthlyPagerAdapter.INIT_MONTH_VISIBLE);
	}

	@Override
	public void setTabs(TabLayout tabs) {
		tabs.setupWithViewPager(pager);
		tabs.setVisibility(View.VISIBLE);
	}
}
