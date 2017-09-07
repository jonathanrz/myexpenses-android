package br.com.jonathanzanella.myexpenses.injection

import android.arch.persistence.room.Room
import android.content.Context
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.*
import br.com.jonathanzanella.myexpenses.bill.*
import br.com.jonathanzanella.myexpenses.card.*
import br.com.jonathanzanella.myexpenses.database.DB_NAME
import br.com.jonathanzanella.myexpenses.database.MyDatabase
import br.com.jonathanzanella.myexpenses.expense.*
import br.com.jonathanzanella.myexpenses.helpers.ResourcesHelper
import br.com.jonathanzanella.myexpenses.receipt.*
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.source.*
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

    @Singleton
    @Provides
    fun providesResourcesHelper(context: Context): ResourcesHelper = ResourcesHelper(context)
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
    fun providesAccountDataSource(accountDao: AccountDao): AccountDataSource = AccountRepository(accountDao)

    @Singleton
    @Provides
    fun providesBillDataSource(billDao: BillDao, expenseRepository: ExpenseRepository): BillDataSource
            = BillRepository(billDao, expenseRepository)

    @Singleton
    @Provides
    fun providesCardDataSource(cardDao: CardDao): CardDataSource = CardRepository(cardDao)

    @Singleton
    @Provides
    fun providesExpenseDataSource(expenseDao: ExpenseDao, cardRepository: CardRepository): ExpenseDataSource
            = ExpenseRepository(expenseDao, cardRepository)

    @Singleton
    @Provides
    fun providesReceipDataSource(receiptDao: ReceiptDao): ReceiptDataSource = ReceiptRepository(receiptDao)

    @Singleton
    @Provides
    fun providesSourceDataSource(sourceDao: SourceDao): SourceDataSource = SourceRepository(sourceDao)
}

@Module
class PresenterModule {
    @Singleton
    @Provides
    fun providesAccountPresenter(dataSource: AccountDataSource) = AccountPresenter(dataSource)

    @Singleton
    @Provides
    fun providesBillPresenter(dataSource: BillDataSource) = BillPresenter(dataSource)

    @Singleton
    @Provides
    fun providesCardPresenter(accountDataSource: AccountDataSource, cardDataSource: CardDataSource,
                              expenseDataSource: ExpenseDataSource, resourcesHelper: ResourcesHelper)
            = CardPresenter(accountDataSource, cardDataSource, expenseDataSource, resourcesHelper)
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