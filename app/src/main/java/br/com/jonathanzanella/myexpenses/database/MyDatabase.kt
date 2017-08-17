package br.com.jonathanzanella.myexpenses.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillDao
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceDao

@Database(entities = arrayOf(Bill::class, Source::class), version = 1)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao
    abstract fun sourceDao(): SourceDao
}