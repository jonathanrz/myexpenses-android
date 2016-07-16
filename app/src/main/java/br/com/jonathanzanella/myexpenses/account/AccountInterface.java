package br.com.jonathanzanella.myexpenses.account;

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
public interface AccountInterface {
    @GET("accounts")
    Call<List<Account>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("accounts")
    Call<Account> create(@Body Account account);
    @PUT("accounts/{id}")
    Call<Account> update(@Path("id") String serverId, @Body Account account);
}