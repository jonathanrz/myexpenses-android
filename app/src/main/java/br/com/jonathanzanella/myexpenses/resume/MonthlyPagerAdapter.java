package br.com.jonathanzanella.myexpenses.resume;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.views.BaseView;

public class MonthlyPagerAdapter extends PagerAdapter {
    @SuppressWarnings("WeakerAccess")
    public static final int TOTAL_MONTHS_VISIBLE = 25;
	public static final int INIT_MONTH_VISIBLE = TOTAL_MONTHS_VISIBLE / 2;

	private final MonthlyPagerAdapterHelper helper = new MonthlyPagerAdapterHelper();
    private final Context context;
    private final List<DateTime> months = new ArrayList<>();
	private final MonthlyPagerAdapterBuilder builder;

    public MonthlyPagerAdapter(Context context, MonthlyPagerAdapterBuilder builder) {
        this.context = context;
	    this.builder = builder;

        DateTime initTime = DateTime.now().minusMonths(INIT_MONTH_VISIBLE).withTime(0,0,0,0).withDayOfMonth(1);

        for(int i = 0; i < TOTAL_MONTHS_VISIBLE; i++) {
            months.add(initTime.plusMonths(i));
        }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        BaseView view = builder.buildView(context, months.get(position));
        collection.addView(view);
        view.refreshData();
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return months.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return helper.formatMonthForView(months.get(position));
    }

	public int getDatePosition(DateTime date) {
		for (int i = 0; i < months.size(); i++) {
			DateTime d = months.get(i);
			if(d.getMonthOfYear() == date.getMonthOfYear() &&
					d.getYear() == date.getYear())
				return i;
		}

		return 0;
	}

	public DateTime getDate(int position) {
		return months.get(position);
	}
}
