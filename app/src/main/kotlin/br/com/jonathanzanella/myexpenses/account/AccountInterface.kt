package br.com.jonathanzanella.myexpenses.account

import retrofit2.Call
import retrofit2.http.*

interface AccountInterface {
    @GET("accounts")
    fun index(@Query("last-updated-at") lastUpdatedAt: Long): Call<List<Account>>

    @POST("accounts")
    fun create(@Body account: Account): Call<Account>

    @PUT("accounts/{id}")
    fun update(@Path("id") serverId: String, @Body account: Account): Call<Account>
}
