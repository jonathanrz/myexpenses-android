package br.com.jonathanzanella.myexpenses.server;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by jzanella on 7/13/16.
 */
public interface ServerInterface {
	@GET("health-check")
	Observable<Void> healthCheck();
}