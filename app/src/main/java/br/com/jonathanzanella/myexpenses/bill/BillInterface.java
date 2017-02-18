package br.com.jonathanzanella.myexpenses.bill;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BillInterface {
    @GET("bills")
    Call<List<Bill>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("bills")
    Call<Bill> create(@Body Bill bill);
    @PUT("bills/{id}")
    Call<Bill> update(@Path("id") String serverId, @Body Bill bill);
}
