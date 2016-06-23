package br.com.jonathanzanella.myexpenses.server;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import br.com.jonathanzanella.myexpenses.BuildConfig;
import br.com.jonathanzanella.myexpenses.Environment;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jzanella on 6/5/16.
 */
public class Server {
	private Retrofit retrofit;

	private class HeaderInterceptor implements Interceptor {
		@Override
		public Response intercept(Chain chain)
				throws IOException {
			Request request = chain.request();
			request = request.newBuilder()
					.addHeader("Auth-token", BuildConfig.MYEXPENSES_AUTH_TOKEN)
					.build();
			return chain.proceed(request);
		}
	}

	public Server() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new HeaderInterceptor()).build();

		retrofit = new Retrofit.Builder()
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(gson))
				.baseUrl(Environment.SERVER_URL)
				.client(client)
				.build();
	}

	public SourceInterface sourceInterface() {
		return retrofit.create(SourceInterface.class);
	}
	public AccountInterface accountInterface() {
		return retrofit.create(AccountInterface.class);
	}
}
