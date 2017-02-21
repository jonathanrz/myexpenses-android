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
import java.util.concurrent.TimeUnit;

import br.com.jonathanzanella.myexpenses.BuildConfig;
import br.com.jonathanzanella.myexpenses.Environment;
import br.com.jonathanzanella.myexpenses.account.AccountInterface;
import br.com.jonathanzanella.myexpenses.bill.BillInterface;
import br.com.jonathanzanella.myexpenses.card.CardInterface;
import br.com.jonathanzanella.myexpenses.expense.ExpenseInterface;
import br.com.jonathanzanella.myexpenses.receipt.ReceiptInterface;
import br.com.jonathanzanella.myexpenses.source.SourceInterface;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
		Gson gson = new GsonBuilder()
							.excludeFieldsWithoutExposeAnnotation()
							.registerTypeAdapter(DateTime.class, dateTimeJsonSerializer)
							.registerTypeAdapter(DateTime.class, dateTimeJsonDeserializer)
							.create();

		OkHttpClient client = new OkHttpClient.Builder()
									.addInterceptor(new HeaderInterceptor())
									.connectTimeout(120, TimeUnit.SECONDS)
									.readTimeout(120, TimeUnit.SECONDS)
									.build();

		retrofit = new Retrofit.Builder()
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create(gson))
				.baseUrl(Environment.SERVER_URL)
				.client(client)
				.build();
	}

	ServerInterface serverInterface() {
		return retrofit.create(ServerInterface.class);
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
	public ExpenseInterface expenseInterface() {
		return retrofit.create(ExpenseInterface.class);
	}
	public ReceiptInterface receiptInterface() {
		return retrofit.create(ReceiptInterface.class);
	}
}
