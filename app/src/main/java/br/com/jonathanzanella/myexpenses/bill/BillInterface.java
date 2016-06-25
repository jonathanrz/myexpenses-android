package br.com.jonathanzanella.myexpenses.bill;

import java.util.List;

import br.com.jonathanzanella.myexpenses.bill.Bill;
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
public interface BillInterface {
    @GET("bills")
    Observable<List<Bill>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("bills")
    Observable<Bill> create(@Body Bill bill);
    @PUT("bills/{id}")
    Observable<Bill> update(@Path("id") String serverId, @Body Bill bill);
}
