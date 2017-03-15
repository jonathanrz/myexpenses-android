package br.com.jonathanzanella.myexpenses.card;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CardInterface {
    @GET("cards")
    Call<List<Card>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("cards")
    Call<Card> create(@Body Card card);
    @PUT("cards/{id}")
    Call<Card> update(@Path("id") String serverId, @Body Card card);
}
