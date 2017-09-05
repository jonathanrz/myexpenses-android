package br.com.jonathanzanella.myexpenses

import android.annotation.SuppressLint
import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import br.com.jonathanzanella.myexpenses.database.DB_NAME
import br.com.jonathanzanella.myexpenses.database.MyDatabase
import br.com.jonathanzanella.myexpenses.helpers.CrashlyticsReportingTree
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import io.fabric.sdk.android.Fabric
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.lang.ref.WeakReference

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        App.context = WeakReference(applicationContext)
        App.database = Room.databaseBuilder(this, MyDatabase::class.java, DB_NAME).build()

        JodaTimeAndroid.init(this)
        Stetho.initializeWithDefaults(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Fabric.with(this, Crashlytics())
            Timber.plant(CrashlyticsReportingTree())
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var context: WeakReference<Context>? = null
        lateinit var database: MyDatabase

        fun getContext(): Context {
            return context!!.get()!!
        }

        fun resetDatabase() {
            database.accountDao().deleteAll()
            database.billDao().deleteAll()
            database.cardDao().deleteAll()
            database.expenseDao().deleteAll()
            database.receiptDao().deleteAll()
            database.sourceDao().deleteAll()
        }
    }
}
