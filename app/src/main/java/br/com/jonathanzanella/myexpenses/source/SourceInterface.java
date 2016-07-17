package br.com.jonathanzanella.myexpenses.source;

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
public interface SourceInterface {
    @GET("sources")
    Call<List<Source>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("sources")
    Call<Source> create(@Body Source source);
    @PUT("sources/{id}")
    Call<Source> update(@Path("id") String serverId, @Body Source source);
}
