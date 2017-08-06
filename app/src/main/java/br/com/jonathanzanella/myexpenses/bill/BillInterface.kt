package br.com.jonathanzanella.myexpenses.bill

import retrofit2.Call
import retrofit2.http.*

interface BillInterface {
    @GET("bills")
    fun index(@Query("last-updated-at") lastUpdatedAt: Long): Call<List<Bill>>

    @POST("bills")
    fun create(@Body bill: Bill): Call<Bill>

    @PUT("bills/{id}")
    fun update(@Path("id") serverId: String, @Body bill: Bill): Call<Bill>
}
