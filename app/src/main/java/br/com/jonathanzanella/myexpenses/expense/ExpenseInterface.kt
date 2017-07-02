package br.com.jonathanzanella.myexpenses.expense

import retrofit2.Call
import retrofit2.http.*

interface ExpenseInterface {
    @GET("expenses")
    fun index(@Query("last-updated-at") lastUpdatedAt: Long): Call<List<Expense>>

    @POST("expenses")
    fun create(@Body expense: Expense): Call<Expense>

    @PUT("expenses/{id}")
    fun update(@Path("id") serverId: String, @Body expense: Expense): Call<Expense>
}
