package br.com.jonathanzanella.myexpenses.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;

import java.io.IOException;
import java.lang.reflect.Type;

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

	JsonSerializer<DateTime> dateTimeJsonSerializer = new JsonSerializer<DateTime>() {
		@Override
		public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext
				context) {
			return src == null ? null : new JsonPrimitive(src.getMillis());
		}
	};

	JsonDeserializer<DateTime> dateTimeJsonDeserializer = new JsonDeserializer<DateTime>() {
		@Override
		public DateTime deserialize(JsonElement json, Type typeOfT,
		                        JsonDeserializationContext context) throws JsonParseException {
			return json == null ? null : new DateTime(json.getAsLong());
		}
	};

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
		Gson gson = new GsonBuilder()
							.excludeFieldsWithoutExposeAnnotation()
							.registerTypeAdapter(DateTime.class, dateTimeJsonSerializer)
							.registerTypeAdapter(DateTime.class, dateTimeJsonDeserializer)
							.create();

		OkHttpClient client = new OkHttpClient.Builder()
									.addInterceptor(new HeaderInterceptor())
									.build();

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
	public BillInterface billInterface() {
		return retrofit.create(BillInterface.class);
	}
	public CardInterface cardInterface() {
		return retrofit.create(CardInterface.class);
	}
}
