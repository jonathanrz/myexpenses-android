package br.com.jonathanzanella.myexpenses.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceDao

@Database(entities = arrayOf(Source::class), version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
}