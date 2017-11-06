package br.com.jonathanzanella.myexpenses.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
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

const val DB_NAME = "data-v3.db"
const val DB_VERSION = 1

@Database(entities = arrayOf(Account::class, Bill::class, Card::class, Expense::class, Receipt::class, Source::class), version = DB_VERSION)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun billDao(): BillDao
    abstract fun cardDao(): CardDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun sourceDao(): SourceDao
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
