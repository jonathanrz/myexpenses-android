package br.com.jonathanzanella.myexpenses.injection

import br.com.jonathanzanella.myexpenses.account.*
import br.com.jonathanzanella.myexpenses.account.transactions.MonthTransactionsView
import br.com.jonathanzanella.myexpenses.bill.*
import br.com.jonathanzanella.myexpenses.card.*
import br.com.jonathanzanella.myexpenses.chargeable.ListChargeableActivity
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper
import br.com.jonathanzanella.myexpenses.expense.*
import br.com.jonathanzanella.myexpenses.overview.OverviewExpensesMonthlyView
import br.com.jonathanzanella.myexpenses.overview.OverviewExpensesWeeklyView
import br.com.jonathanzanella.myexpenses.receipt.*
import br.com.jonathanzanella.myexpenses.resume.ResumeMonthlyView
import br.com.jonathanzanella.myexpenses.source.EditSourceActivity
import br.com.jonathanzanella.myexpenses.source.ShowSourceActivity
import br.com.jonathanzanella.myexpenses.source.SourceAdapter
import br.com.jonathanzanella.myexpenses.source.SourceDataSource
import br.com.jonathanzanella.myexpenses.sync.SyncService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class), (DatabaseModule::class), (RepositoryModule::class), (PresenterModule::class), (ServerModule::class), (AccountModule::class), (BillModule::class)])
interface AppComponent {
    fun accountDataSource(): AccountDataSource
    fun billDataSource(): BillDataSource
    fun cardDataSource(): CardDataSource
    fun expenseDataSource(): ExpenseDataSource
    fun receiptDataSource(): ReceiptDataSource
    fun sourceDataSource(): SourceDataSource

    fun inject(databaseHelper: DatabaseHelper)

    fun inject(accountView: AccountView)
    fun inject(editAccountActivity: EditAccountActivity)
    fun inject(showAccountActivity: ShowAccountActivity)
    fun inject(listAccountActivity: ListAccountActivity)
    fun inject(listChargeableActivity: ListChargeableActivity)

    fun inject(billView: BillView)
    fun inject(billAdapter: BillAdapter)
    fun inject(billMonthlyResumeAdapter: BillMonthlyResumeAdapter)
    fun inject(editBillActivity: EditBillActivity)
    fun inject(showBillActivity: ShowBillActivity)
    fun inject(listBillActivity: ListBillActivity)

    fun inject(card: Card)
    fun inject(cardAdapter: CardAdapter)
    fun inject(creditCardInvoiceActivity: CreditCardInvoiceActivity)
    fun inject(editCardActivity: EditCardActivity)
    fun inject(showCardActivity: ShowCardActivity)
    fun inject(creditCardMonthlyAdapter: CreditCardMonthlyAdapter)

    fun inject(expense: Expense)
    fun inject(editExpenseActivity: EditExpenseActivity)
    fun inject(expenseAdapter: ExpenseAdapter)
    fun inject(expenseView: ExpenseView)
    fun inject(showExpenseActivity: ShowExpenseActivity)
    fun inject(overviewExpensesMonthlyView: OverviewExpensesMonthlyView)
    fun inject(overviewExpensesWeeklyView: OverviewExpensesWeeklyView)

    fun inject(editReceiptActivity: EditReceiptActivity)
    fun inject(receipt: Receipt)
    fun inject(receiptAdapter: ReceiptAdapter)
    fun inject(receiptView: ReceiptView)
    fun inject(showReceiptActivity: ShowReceiptActivity)

    fun inject(resumeMonthlyView: ResumeMonthlyView)
    fun inject(monthTransactionsView: MonthTransactionsView)

    fun inject(editSourceActivity: EditSourceActivity)
    fun inject(sourceAdapter: SourceAdapter)
    fun inject(showSourceActivity: ShowSourceActivity)

    fun inject(syncService: SyncService)
}
