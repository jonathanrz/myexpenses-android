package br.com.jonathanzanella.myexpenses.injection

import android.arch.persistence.room.Room
import android.content.Context
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.AccountDao
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.bill.BillDao
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.card.CardDao
import br.com.jonathanzanella.myexpenses.card.CardRepository
import br.com.jonathanzanella.myexpenses.database.DB_NAME
import br.com.jonathanzanella.myexpenses.database.MyDatabase
import br.com.jonathanzanella.myexpenses.expense.ExpenseDao
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDao
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.source.SourceDao
import br.com.jonathanzanella.myexpenses.source.SourceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class AppModule(private val app: App) {
    @Singleton
    @Provides
    fun providesApp(): App = app

    @Singleton
    @Provides
    fun providesContext(): Context = app
}

@Module
open class DatabaseModule {
    @Singleton
    @Provides
    fun providesDatabase(context: Context): MyDatabase = Room.databaseBuilder(context, MyDatabase::class.java, DB_NAME).build()

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

@Module
open class RepositoryModule {
    @Singleton
    @Provides
    fun providesAccountRepository(accountDao: AccountDao) = AccountRepository(accountDao)

    @Singleton
    @Provides
    fun providesBillRepository(billDao: BillDao, expenseRepository: ExpenseRepository) = BillRepository(billDao, expenseRepository)

    @Singleton
    @Provides
    fun providesCardRepository(cardDao: CardDao) = CardRepository(cardDao)

    @Singleton
    @Provides
    fun providesExpenseRepository(expenseDao: ExpenseDao, cardRepository: CardRepository) = ExpenseRepository(expenseDao, cardRepository)

    @Singleton
    @Provides
    fun providesReceiptRepository(receiptDao: ReceiptDao) = ReceiptRepository(receiptDao)

    @Singleton
    @Provides
    fun providesSourceRepository(sourceDao: SourceDao) = SourceRepository(sourceDao)
}