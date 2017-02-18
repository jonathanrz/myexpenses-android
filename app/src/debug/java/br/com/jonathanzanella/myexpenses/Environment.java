package br.com.jonathanzanella.myexpenses;

import java.util.Locale;

import br.com.jonathanzanella.myexpenses.user.Users;

public interface Environment {
	Locale PTBR_LOCALE = new Locale("pt_BR");
	String SERVER_URL = "http://192.168.0.12:3000/";
	String CURRENT_USER = "Jonathan";
	String CURRENT_USER_UUID = Users.JONATHAN;
	long SYNC_PERIODIC_EXECUTION_FREQUENCY = 15L * 60L;
	long SYNC_FLEX_EXECUTION = 2L * 60L;
	String DB_NAME = "MyExpenses.db";
	int DB_VERSION = 1;
}