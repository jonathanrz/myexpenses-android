package br.com.jonathanzanella.myexpenses.server;

import java.util.List;

import br.com.jonathanzanella.myexpenses.models.Expense;
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
public interface ExpenseInterface {
    @GET("expenses")
    Observable<List<Expense>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("expenses")
    Observable<Expense> create(@Body Expense expense);
    @PUT("expenses/{id}")
    Observable<Expense> update(@Path("id") String serverId, @Body Expense expense);
}
