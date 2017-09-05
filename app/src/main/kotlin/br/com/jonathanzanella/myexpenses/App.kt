package br.com.jonathanzanella.myexpenses

import android.app.Application
import android.content.Context
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper
import br.com.jonathanzanella.myexpenses.helpers.CrashlyticsReportingTree
import br.com.jonathanzanella.myexpenses.injection.AppComponent
import br.com.jonathanzanella.myexpenses.injection.AppModule
import br.com.jonathanzanella.myexpenses.injection.DaggerAppComponent
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import io.fabric.sdk.android.Fabric
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.lang.ref.WeakReference

class App : Application() {
    lateinit var appComponent: AppComponent
    lateinit var databaseHelper: DatabaseHelper

    override fun onCreate() {
        super.onCreate()
        App.app = WeakReference(this)

        JodaTimeAndroid.init(this)
        Stetho.initializeWithDefaults(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Fabric.with(this, Crashlytics())
            Timber.plant(CrashlyticsReportingTree())
        }

        buildComponent()
    }

    private fun buildComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        appComponent.inject(databaseHelper)
    }

    companion object {
        private var app: WeakReference<App>? = null

        fun getContext(): Context {
            return app!!.get()!!
        }

        fun resetDatabase() {
            app!!.get()!!.databaseHelper.resetDatabase()
        }
    }
}
