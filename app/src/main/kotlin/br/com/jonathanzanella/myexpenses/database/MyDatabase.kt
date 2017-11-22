package br.com.jonathanzanella.myexpenses.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import android.content.Context
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDao
import br.com.jonathanzanella.myexpenses.account.AccountDataSource
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillDao
import br.com.jonathanzanella.myexpenses.bill.BillDataSource
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardDao
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseDao
import br.com.jonathanzanella.myexpenses.injection.AppComponent
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDao
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceDao
import javax.inject.Inject

const val DB_NAME = "data-v5.db"
const val DB_VERSION = 2

@Database(entities = [(Account::class), (Bill::class), (Card::class), (Expense::class), (Receipt::class), (Source::class)], version = DB_VERSION)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun billDao(): BillDao
    abstract fun cardDao(): CardDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun sourceDao(): SourceDao

    companion object {
        fun buildDatabase(context: Context, dbName: String = DB_NAME): MyDatabase =
                Room.databaseBuilder(context, MyDatabase::class.java, dbName)
                        .addMigrations(MIGRATION_1_2)
                        .build()

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Account " + " ADD COLUMN removed INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}

class DatabaseHelper(appComponent: AppComponent) {
    @Inject lateinit var accountDataSource: AccountDataSource
    @Inject lateinit var billDataSource: BillDataSource
    @Inject lateinit var cardDao: CardDao
    @Inject lateinit var expenseDao: ExpenseDao
    @Inject lateinit var receiptDao: ReceiptDao
    @Inject lateinit var sourceDao: SourceDao

    init {
        appComponent.inject(this)
    }

    fun resetDatabase() {
        accountDataSource.deleteAll()
        billDataSource.deleteAll()
        cardDao.deleteAll()
        expenseDao.deleteAll()
        receiptDao.deleteAll()
        sourceDao.deleteAll()
    }
}
