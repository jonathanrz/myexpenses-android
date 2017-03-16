package br.com.jonathanzanella.myexpenses.helpers;

import java.text.NumberFormat;

public final class CurrencyHelper {
	private static final double TOTAL_CENTS = 100.0;
	private CurrencyHelper() {}

	public static String format(int cents) {
		return NumberFormat.getCurrencyInstance().format(cents / TOTAL_CENTS);
	}

	static String format(double cents) {
		return NumberFormat.getCurrencyInstance().format(cents / TOTAL_CENTS);
	}
}
