package br.com.jonathanzanella.myexpenses.expense;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExpenseInterface {
    @GET("expenses")
    Call<List<Expense>> index(@Query("last-updated-at") long lastUpdatedAt);
    @POST("expenses")
    Call<Expense> create(@Body Expense expense);
    @PUT("expenses/{id}")
    Call<Expense> update(@Path("id") String serverId, @Body Expense expense);
}
