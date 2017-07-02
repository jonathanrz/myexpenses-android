package br.com.jonathanzanella.myexpenses.sync

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class ServerData(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @SuppressLint("ApplySharedPref")
    internal fun updateInfo(serverUrl: String, serverToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString(SERVER_URL, serverUrl)
        editor.putString(SERVER_TOKEN, serverToken)
        editor.commit()
    }

    val serverUrl: String
        get() = sharedPreferences.getString(SERVER_URL, "")

    val serverToken: String
        get() = sharedPreferences.getString(SERVER_TOKEN, "")

    companion object {
        private val SERVER_URL = "ServerUrl"
        private val SERVER_TOKEN = "ServerToken"
    }
}
