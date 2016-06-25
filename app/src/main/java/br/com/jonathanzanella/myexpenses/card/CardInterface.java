package br.com.jonathanzanella.myexpenses.card;

import java.util.List;

import br.com.jonathanzanella.myexpenses.card.Card;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jzanella on 6/5/16.
 */
public interface CardInterface {
    @GET("cards")
    Observable<List<Card>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("cards")
    Observable<Card> create(@Body Card card);
    @PUT("cards/{id}")
    Observable<Card> update(@Path("id") String serverId, @Body Card card);
}
