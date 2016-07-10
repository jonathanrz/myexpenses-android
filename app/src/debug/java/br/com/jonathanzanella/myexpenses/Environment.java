package br.com.jonathanzanella.myexpenses;

import java.util.Locale;

/**
 * Created by Jonathan Zanella on 07/02/16.
 */
public interface Environment {
	boolean IS_DEBUG = true;
	Locale PTBR_LOCALE = new Locale("pt_BR");
	String SERVER_URL = "http://192.168.0.13:3000/";
	String CURRENT_USER = "Jonathan";
}