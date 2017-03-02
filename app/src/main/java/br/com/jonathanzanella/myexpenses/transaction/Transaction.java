package br.com.jonathanzanella.myexpenses.transaction;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Transaction {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

	String getName();
	DateTime getDate();
	int getAmount();
	boolean credited();
	boolean debited();
}
