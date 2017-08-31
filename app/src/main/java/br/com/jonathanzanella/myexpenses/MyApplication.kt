package br.com.jonathanzanella.myexpenses

import android.annotation.SuppressLint
import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper
import br.com.jonathanzanella.myexpenses.database.MyDatabase
import com.facebook.stetho.Stetho
import net.danlew.android.joda.JodaTimeAndroid
import java.lang.ref.WeakReference

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MyApplication.context = WeakReference(applicationContext)
        MyApplication.database = Room.databaseBuilder(this, MyDatabase::class.java, "Expenses.db").build()

        JodaTimeAndroid.init(this)
        Stetho.initializeWithDefaults(this)
        DatabaseHelper(this).runMigrations()
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