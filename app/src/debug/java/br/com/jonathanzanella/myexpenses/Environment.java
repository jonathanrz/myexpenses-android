package br.com.jonathanzanella.myexpenses;

import java.util.Locale;

public interface Environment {
	Locale PTBR_LOCALE = new Locale("pt_BR");
	long SYNC_PERIODIC_EXECUTION_FREQUENCY = 15L * 60L;
	long SYNC_FLEX_EXECUTION = 2L * 60L;
}