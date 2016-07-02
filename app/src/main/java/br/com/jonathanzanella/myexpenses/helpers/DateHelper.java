package br.com.jonathanzanella.myexpenses.helpers;

import org.joda.time.DateTime;

/**
 * Created by jzanella on 7/1/16.
 */
public class DateHelper {
	public static DateTime firstDayOfMonth(DateTime date) {
		return date.dayOfMonth().withMinimumValue().withMillisOfDay(0);
	}

	public static DateTime lastDayOfMonth(DateTime date) {
		return date.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
	}
}