package br.com.jonathanzanella.myexpenses;

import java.util.Locale;

public interface Environment {
	Locale PTBR_LOCALE = new Locale("pt_BR");
	long SYNC_PERIODIC_EXECUTION_FREQUENCY = 24L * 60L * 60L;
	long SYNC_FLEX_EXECUTION = 30L * 60L;
}