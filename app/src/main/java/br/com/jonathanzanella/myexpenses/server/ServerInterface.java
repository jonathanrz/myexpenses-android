package br.com.jonathanzanella.myexpenses.server;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by jzanella on 7/13/16.
 */
public interface ServerInterface {
	@GET("health-check")
	Call<Void> healthCheck();
}