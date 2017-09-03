package br.com.jonathanzanella.myexpenses.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.account.AccountDao
import br.com.jonathanzanella.myexpenses.bill.Bill
import br.com.jonathanzanella.myexpenses.bill.BillDao
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardDao
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseDao
import br.com.jonathanzanella.myexpenses.receipt.Receipt
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDao
import br.com.jonathanzanella.myexpenses.source.Source
import br.com.jonathanzanella.myexpenses.source.SourceDao

const val DB_NAME = "data.db"
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
