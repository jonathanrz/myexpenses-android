package br.com.jonathanzanella.myexpenses;

import java.util.Locale;

import br.com.jonathanzanella.myexpenses.user.Users;

/**
 * Created by Jonathan Zanella on 07/02/16.
 */
public interface Environment {
	boolean IS_DEBUG = false;
	Locale PTBR_LOCALE = new Locale("pt_BR");
	String SERVER_URL = "https://jonathanzanella-myexpenses.herokuapp.com/";
	String CURRENT_USER = "Thainara";
	String CURRENT_USER_UUID = Users.THAINARA;
}