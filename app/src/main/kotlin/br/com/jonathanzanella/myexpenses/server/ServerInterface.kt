package br.com.jonathanzanella.myexpenses.server

import retrofit2.Call
import retrofit2.http.GET

internal interface ServerInterface {
    @GET("health-check")
    fun healthCheck(): Call<Void>
}
