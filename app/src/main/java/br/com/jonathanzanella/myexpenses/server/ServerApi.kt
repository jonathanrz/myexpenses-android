package br.com.jonathanzanella.myexpenses.server

import android.util.Log
import br.com.jonathanzanella.myexpenses.MyApplication
import java.io.IOException

class ServerApi {
    private val serverInterface: ServerInterface by lazy {
        Server(MyApplication.getContext()).serverInterface()
    }

    fun healthCheck(): Boolean {
        val caller = serverInterface.healthCheck()
        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                return true
            } else {
                Log.e(LOG_TAG, "Error in health-check: " + response.code() + " " + response.message())
                return false
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error in health-check:" + e.message)
            e.printStackTrace()
            return false
        }

    }

    companion object {
        private val LOG_TAG = ServerApi::class.java.simpleName
    }
}