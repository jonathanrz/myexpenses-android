package br.com.jonathanzanella.myexpenses.injection

import br.com.jonathanzanella.myexpenses.database.DatabaseHelper
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, DatabaseModule::class))
interface AppComponent {
    fun inject(databaseHelper: DatabaseHelper)
}