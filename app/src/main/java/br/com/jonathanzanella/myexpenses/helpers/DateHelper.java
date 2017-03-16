package br.com.jonathanzanella.myexpenses.helpers;

import org.joda.time.DateTime;

public final class DateHelper {
	private static final int MAX_HOURS_OF_DAY = 23;
	private static final int MAX_MINUTES_OF_HOUR = 59;
	private static final int MAX_SECONDS_OF_MINUTE = 59;
	private static final int MAX_MILLISECONDS_OF_SECOND = 999;

	private DateHelper() {}

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
		return date.withTime(MAX_HOURS_OF_DAY, MAX_MINUTES_OF_HOUR, MAX_SECONDS_OF_MINUTE, MAX_MILLISECONDS_OF_SECOND);
	}
}