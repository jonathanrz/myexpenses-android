package br.com.jonathanzanella.myexpenses.injection

import android.arch.persistence.room.Room
import android.content.Context
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.database.DB_NAME
import br.com.jonathanzanella.myexpenses.database.MyDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {
    @Singleton
    @Provides
    fun providesApp(): App = app

    @Singleton
    @Provides
    fun providesContext(): Context = app
}

@Module
class DatabaseModule() {
    @Singleton
    @Provides
    fun providesDatabase(context: Context) = Room.databaseBuilder(context, MyDatabase::class.java, DB_NAME).build()

    @Singleton
    @Provides
    fun providesAccountDao(database: MyDatabase) = database.accountDao()

    @Singleton
    @Provides
    fun providesBillDao(database: MyDatabase) = database.billDao()

    @Singleton
    @Provides
    fun providesCardDao(database: MyDatabase) = database.cardDao()

    @Singleton
    @Provides
    fun providesExpenseDao(database: MyDatabase) = database.expenseDao()

    @Singleton
    @Provides
    fun providesReceiptDao(database: MyDatabase) = database.receiptDao()

    @Singleton
    @Provides
    fun providesSourceDao(database: MyDatabase) = database.sourceDao()
}