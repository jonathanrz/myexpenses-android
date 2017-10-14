package br.com.jonathanzanella.myexpenses.server

import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ServerApi @Inject constructor(private val serverInterface: ServerInterface) {
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
