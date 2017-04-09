package br.com.jonathanzanella.myexpenses.server;

import java.io.IOException;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.log.Log;
import retrofit2.Call;
import retrofit2.Response;

public class ServerApi {
	private static final String LOG_TAG = ServerApi.class.getSimpleName();
	private ServerInterface serverInterface;

	private ServerInterface getInterface() {
		if(serverInterface == null)
			serverInterface = new Server(MyApplication.getContext()).serverInterface();
		return serverInterface;
	}

	public boolean healthCheck() {
		Call<Void> caller = getInterface().healthCheck();
		try {
			Response<Void> response = caller.execute();
			if(response.isSuccessful()) {
				return true;
			} else {
				Log.error(LOG_TAG, "Error in health-check: " + response.code() + " " + response.message());
				return false;
			}
		} catch (IOException e) {
			Log.error(LOG_TAG, "Error in health-check:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}