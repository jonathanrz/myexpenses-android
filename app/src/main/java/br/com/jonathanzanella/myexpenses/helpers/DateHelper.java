package br.com.jonathanzanella.myexpenses.helpers;

import org.joda.time.DateTime;

/**
 * Created by jzanella on 7/1/16.
 */
public class DateHelper {
	public static DateTime firstDayOfMonth(DateTime date) {
		return firstMillisOfDay(date.dayOfMonth().withMinimumValue());
	}

	public static DateTime firstMillisOfDay(DateTime date) {
		return date.withMillisOfDay(0);
	}

	public static DateTime lastDayOfMonth(DateTime date) {
		return lastMillisOfDay(date.dayOfMonth().withMaximumValue());
	}

	public static DateTime lastMillisOfDay(DateTime date) {
		return date.withTime(23, 59, 59, 999);
	}
}