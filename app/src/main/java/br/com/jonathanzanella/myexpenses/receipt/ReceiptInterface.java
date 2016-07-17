package br.com.jonathanzanella.myexpenses.receipt;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jzanella on 6/5/16.
 */
public interface ReceiptInterface {
    @GET("receipts")
    Call<List<Receipt>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("receipts")
    Call<Receipt> create(@Body Receipt receipt);
    @PUT("receipts/{id}")
    Call<Receipt> update(@Path("id") String serverId, @Body Receipt receipt);
}
