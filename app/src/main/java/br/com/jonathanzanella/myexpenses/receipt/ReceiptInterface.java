package br.com.jonathanzanella.myexpenses.receipt;

import java.util.List;

import br.com.jonathanzanella.myexpenses.receipt.Receipt;
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
public interface ReceiptInterface {
    @GET("receipts")
    Observable<List<Receipt>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("receipts")
    Observable<Receipt> create(@Body Receipt receipt);
    @PUT("receipts/{id}")
    Observable<Receipt> update(@Path("id") String serverId, @Body Receipt receipt);
}
