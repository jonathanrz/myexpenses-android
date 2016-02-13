package br.com.jonathanzanella.myexpenses.models;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Jonathan Zanella on 13/02/16.
 */
public interface Transaction {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

	String getName();
	DateTime getDate();
	int getAmount();
	boolean credited();
	boolean debited();
}
