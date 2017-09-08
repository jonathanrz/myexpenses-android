package br.com.jonathanzanella.myexpenses.server

import br.com.jonathanzanella.myexpenses.App
import timber.log.Timber
import java.io.IOException

class ServerApi {
    private val serverInterface: ServerInterface by lazy {
        Server(App.getContext()).serverInterface()
    }

    fun healthCheck(): Boolean {
        val caller = serverInterface.healthCheck()
        return try {
            val response = caller.execute()
            if (response.isSuccessful) {
                true
            } else {
                Timber.e("Error in health-check: " + response.code() + " " + response.message())
                false
            }
        } catch (e: IOException) {
            Timber.e("Error in health-check:" + e.message)
            e.printStackTrace()
            false
        }
    }
}
