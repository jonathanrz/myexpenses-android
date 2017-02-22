package br.com.jonathanzanella.myexpenses.expense;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.Repository;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapter;
import br.com.jonathanzanella.myexpenses.resume.MonthlyPagerAdapterBuilder;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExpenseView extends BaseView implements ViewPager.OnPageChangeListener {
	private static final int REQUEST_ADD_EXPENSE = 1006;
	@Bind(R.id.view_expenses_pager)
    ViewPager pager;
	MonthlyPagerAdapter adapter;
	ExpenseRepository expenseRepository;

	private Map<DateTime, WeakReference<ExpenseMonthlyView>> views = new HashMap<>();

    public ExpenseView(Context context) {
        super(context);
    }

    public ExpenseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpenseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_expenses, this);
	    expenseRepository = new ExpenseRepository(new Repository<Expense>(getContext()));
        ButterKnife.bind(this);

        adapter = new MonthlyPagerAdapter(getContext(), new MonthlyPagerAdapterBuilder() {
	        @Override
	        public BaseView buildView(Context ctx, DateTime date) {
		        ExpenseMonthlyView view = new ExpenseMonthlyView(ctx, date);
		        views.put(date, new WeakReference<>(view));
		        view.filter(filter);
		        return view;
	        }
        });
        pager.setAdapter(adapter);
        pager.setCurrentItem(MonthlyPagerAdapter.INIT_MONTH_VISIBLE);
	    pager.addOnPageChangeListener(this);
    }

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		filter(filter);
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void setTabs(TabLayout tabs) {
		tabs.setupWithViewPager(pager);
		tabs.setVisibility(View.VISIBLE);
	}

	@OnClick(R.id.view_expenses_fab)
	void onFab() {
		Context ctx = getContext();
		Intent i = new Intent(getContext(), EditExpenseActivity.class);
		if(ctx instanceof Activity) {
			((Activity) ctx).startActivityForResult(i, REQUEST_ADD_EXPENSE);
		} else {
			ctx.startActivity(i);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case REQUEST_ADD_EXPENSE:
				if(resultCode == Activity.RESULT_OK) {
					new AsyncTask<Void, Void, Expense>() {

						@Override
						protected Expense doInBackground(Void... voids) {
							return expenseRepository.find(data.getStringExtra(EditExpenseActivity.KEY_EXPENSE_UUID));
						}

						@Override
						protected void onPostExecute(Expense expense) {
							super.onPostExecute(expense);
							if(expense != null) {
								ExpenseMonthlyView view = getMonthView(expense.getDate());
								if (view != null)
									view.refreshData();
							}
						}
					}.execute();
				}
				break;
		}
	}

	@Override
	public void filter(String s) {
		super.filter(s);
		DateTime date = adapter.getDate(pager.getCurrentItem());
		ExpenseMonthlyView view = getMonthView(date);
		if (view != null)
			view.filter(filter);
	}

	private @Nullable ExpenseMonthlyView getMonthView(DateTime date) {
		for (Map.Entry<DateTime, WeakReference<ExpenseMonthlyView>> pair : views.entrySet()) {
			DateTime viewDateFirstDay = DateHelper.firstDayOfMonth(pair.getKey());
			DateTime viewDateLastDay = DateHelper.lastDayOfMonth(pair.getKey());
			if (date.isAfter(viewDateFirstDay) && date.isBefore(viewDateLastDay))
				return pair.getValue().get();
		}

		return null;
	}
}