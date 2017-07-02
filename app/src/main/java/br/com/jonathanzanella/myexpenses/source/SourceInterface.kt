package br.com.jonathanzanella.myexpenses.source

import retrofit2.Call
import retrofit2.http.*

interface SourceInterface {
    @GET("sources")
    fun index(@Query("last-updated-at") lastUpdatedAt: Long): Call<List<Source>>

    @POST("sources")
    fun create(@Body source: Source): Call<Source>

    @PUT("sources/{id}")
    fun update(@Path("id") serverId: String, @Body source: Source): Call<Source>
}
