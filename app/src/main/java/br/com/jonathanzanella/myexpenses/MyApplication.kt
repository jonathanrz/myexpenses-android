package br.com.jonathanzanella.myexpenses

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper
import com.facebook.stetho.Stetho
import net.danlew.android.joda.JodaTimeAndroid
import java.lang.ref.WeakReference

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MyApplication.context = WeakReference(applicationContext)

        JodaTimeAndroid.init(this)
        Stetho.initializeWithDefaults(this)
        DatabaseHelper(this).runMigrations()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var context: WeakReference<Context>? = null

        fun getContext(): Context {
            return context!!.get()!!
        }
    }
}