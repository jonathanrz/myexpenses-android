package br.com.jonathanzanella.myexpenses.resume;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.views.BaseView;

/**
 * Created by Jonathan Zanella on 03/02/16.
 */
public class MonthlyPagerAdapter extends PagerAdapter {
    public static final int TOTAL_MONTHS_VISIBLE = 25;
    public static final int INIT_MONTH_VISIBLE = TOTAL_MONTHS_VISIBLE / 2;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM/yy", Locale.getDefault());
    private Context context;
    private List<DateTime> months = new ArrayList<>();
	private MonthlyPagerAdapterBuilder builder;

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
        return sdf.format(months.get(position).toDate());
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
