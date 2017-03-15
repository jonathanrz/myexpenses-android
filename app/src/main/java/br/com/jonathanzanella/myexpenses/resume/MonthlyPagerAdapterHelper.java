package br.com.jonathanzanella.myexpenses.resume;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

class MonthlyPagerAdapterHelper {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MMM/yy", Locale.getDefault());

	String formatMonthForView(DateTime month) {
		return sdf.format(month.toDate());
	}
}