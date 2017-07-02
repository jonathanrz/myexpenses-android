package br.com.jonathanzanella.myexpenses.overview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.helpers.DateHelper;
import br.com.jonathanzanella.myexpenses.views.BaseView;

public class WeeklyPagerAdapter extends PagerAdapter {
	private static final int TOTAL_DAYS_OF_WEEK = 7;
	private static final int LAST_DAY_OF_WEEK = 6;
	private final List<Period> periods = new ArrayList<>();
	private final WeeklyPagerAdapterBuilder builder;
    private final Context context;

	public static class Period {
		public DateTime init;
		public DateTime end;

		String titleize() {
			return String.format(Environment.PTBR_LOCALE, "%02d - %02d", init.getDayOfMonth(), end.getDayOfMonth());
		}
	}

    WeeklyPagerAdapter(Context context, DateTime month, WeeklyPagerAdapterBuilder builder) {
        this.context = context;
	    this.builder = builder;

	    DateTime init = DateHelper.INSTANCE.firstMillisOfDay(month.withDayOfMonth(1));

	    while(init.getMonthOfYear() == month.getMonthOfYear()) {
		    Period period = new Period();
		    period.init = init;
		    period.end = init.plusDays(LAST_DAY_OF_WEEK);
		    if(period.end.getMonthOfYear() > month.getMonthOfYear()) {
			    period.end.minusMonths(1);
			    period.end = DateHelper.INSTANCE.lastMillisOfDay(init.dayOfMonth().withMaximumValue());
		    }
		    init = init.plusDays(TOTAL_DAYS_OF_WEEK);
		    periods.add(period);
	    }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        BaseView view = builder.buildView(context, periods.get(position));
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
        return periods.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return periods.get(position).titleize();
    }

	int getPositionOfDay(int day) {
		for (int i = 0; i < periods.size(); i++) {
			Period period = periods.get(i);
			if(day >= period.init.getDayOfMonth()  && day <= period.end.getDayOfMonth())
				return i;
		}

		return 0;
	}
}
