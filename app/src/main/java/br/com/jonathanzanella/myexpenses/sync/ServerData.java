package br.com.jonathanzanella.myexpenses.sync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServerData {
	private static final String SERVER_URL = "ServerUrl";
	private static final String SERVER_TOKEN = "ServerToken";

	private SharedPreferences sharedPreferences;

	public ServerData(Context context) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	@SuppressLint("ApplySharedPref")
	public void updateInfo(String serverUrl, String serverToken) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(SERVER_URL, serverUrl);
		editor.putString(SERVER_TOKEN, serverToken);
		editor.commit();
	}

	public String getServerUrl() {
		return sharedPreferences.getString(SERVER_URL, "");
	}

	public String getServerToken() {
		return sharedPreferences.getString(SERVER_TOKEN, "");
	}
}
