package br.com.jonathanzanella.myexpenses.injection

import android.arch.persistence.room.Room
import android.content.Context
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.AccountApi
import br.com.jonathanzanella.myexpenses.account.AccountDao
import br.com.jonathanzanella.myexpenses.account.AccountInterface
import br.com.jonathanzanella.myexpenses.account.AccountRepository
import br.com.jonathanzanella.myexpenses.bill.BillApi
import br.com.jonathanzanella.myexpenses.bill.BillDao
import br.com.jonathanzanella.myexpenses.bill.BillInterface
import br.com.jonathanzanella.myexpenses.bill.BillRepository
import br.com.jonathanzanella.myexpenses.card.CardApi
import br.com.jonathanzanella.myexpenses.card.CardDao
import br.com.jonathanzanella.myexpenses.card.CardInterface
import br.com.jonathanzanella.myexpenses.card.CardRepository
import br.com.jonathanzanella.myexpenses.database.DB_NAME
import br.com.jonathanzanella.myexpenses.database.MyDatabase
import br.com.jonathanzanella.myexpenses.expense.ExpenseApi
import br.com.jonathanzanella.myexpenses.expense.ExpenseDao
import br.com.jonathanzanella.myexpenses.expense.ExpenseInterface
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.receipt.ReceiptApi
import br.com.jonathanzanella.myexpenses.receipt.ReceiptDao
import br.com.jonathanzanella.myexpenses.receipt.ReceiptInterface
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepository
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.source.SourceApi
import br.com.jonathanzanella.myexpenses.source.SourceDao
import br.com.jonathanzanella.myexpenses.source.SourceInterface
import br.com.jonathanzanella.myexpenses.source.SourceRepository
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
class DatabaseModule {
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
class RepositoryModule {
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

@Module
class ServerModule {
    @Singleton
    @Provides
    fun providesServer(context: Context) = Server(context)

    @Singleton
    @Provides
    fun providesAccountInterface(server: Server) = server.accountInterface()

    @Singleton
    @Provides
    fun providesBillInterface(server: Server) = server.billInterface()

    @Singleton
    @Provides
    fun providesCardInterface(server: Server) = server.cardInterface()

    @Singleton
    @Provides
    fun providesExpenseInterface(server: Server) = server.expenseInterface()

    @Singleton
    @Provides
    fun providesReceiptInterface(server: Server) = server.receiptInterface()

    @Singleton
    @Provides
    fun providesSourceInterface(server: Server) = server.sourceInterface()

    @Singleton
    @Provides
    fun providesAccountApi(accountInterface: AccountInterface, accountRepository: AccountRepository) = AccountApi(accountInterface, accountRepository)

    @Singleton
    @Provides
    fun providesBillApi(billInterface: BillInterface, billRepository: BillRepository) = BillApi(billInterface, billRepository)

    @Singleton
    @Provides
    fun providesCardApi(cardInterface: CardInterface, cardRepository: CardRepository) = CardApi(cardInterface, cardRepository)

    @Singleton
    @Provides
    fun providesExpenseApi(expenseInterface: ExpenseInterface, expenseRepository: ExpenseRepository) = ExpenseApi(expenseInterface, expenseRepository)

    @Singleton
    @Provides
    fun providesReceiptApi(receiptInterface: ReceiptInterface, receiptRepository: ReceiptRepository) = ReceiptApi(receiptInterface, receiptRepository)

    @Singleton
    @Provides
    fun providesSourceApi(sourceInterface: SourceInterface, sourceRepository: SourceRepository) = SourceApi(sourceInterface, sourceRepository)
}