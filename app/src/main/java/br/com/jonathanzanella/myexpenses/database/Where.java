package br.com.jonathanzanella.myexpenses.database;

import android.util.Pair;

/**
 * Created by jzanella on 11/10/16.
 */

public class Where {
	private Fields field;

	public Where(Fields field) {
		this.field = field;
	}

	public Pair<String, String[]> eq(String s) {
		return new Pair<>(field + " = ?", new String[] { s });
	}

	public Pair<String, String[]> eq(Long l) {
		return new Pair<>(field + " = ?", new String[] { String.valueOf(l) });
	}

	public Pair<String, String[]> eq(Boolean b) {
		return new Pair<>(field + " = ?", new String[] { String.valueOf(b ? 1 : 0) });
	}
}