package br.com.jonathanzanella.myexpenses.resume;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

class MonthlyPagerAdapterHelper {
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MMM/yy", Locale.getDefault());

	String formatMonthForView(DateTime month) {
		return SIMPLE_DATE_FORMAT.format(month.toDate());
	}
}