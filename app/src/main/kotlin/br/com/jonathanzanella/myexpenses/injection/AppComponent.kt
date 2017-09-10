package br.com.jonathanzanella.myexpenses.injection

import br.com.jonathanzanella.myexpenses.account.AccountAdapter
import br.com.jonathanzanella.myexpenses.account.EditAccountActivity
import br.com.jonathanzanella.myexpenses.account.ShowAccountActivity
import br.com.jonathanzanella.myexpenses.account.transactions.MonthTransactionsPresenter
import br.com.jonathanzanella.myexpenses.bill.BillAdapter
import br.com.jonathanzanella.myexpenses.bill.BillMonthlyResumeAdapter
import br.com.jonathanzanella.myexpenses.bill.EditBillActivity
import br.com.jonathanzanella.myexpenses.bill.ShowBillActivity
import br.com.jonathanzanella.myexpenses.card.*
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper
import br.com.jonathanzanella.myexpenses.expense.*
import br.com.jonathanzanella.myexpenses.overview.OverviewExpensesMonthlyView
import br.com.jonathanzanella.myexpenses.overview.OverviewExpensesWeeklyView
import br.com.jonathanzanella.myexpenses.receipt.*
import br.com.jonathanzanella.myexpenses.resume.ResumeMonthlyView
import br.com.jonathanzanella.myexpenses.source.EditSourceActivity
import br.com.jonathanzanella.myexpenses.source.ShowSourceActivity
import br.com.jonathanzanella.myexpenses.source.SourceAdapter
import br.com.jonathanzanella.myexpenses.sync.SyncService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, DatabaseModule::class, RepositoryModule::class, PresenterModule::class, ServerModule::class))
interface AppComponent {
    fun inject(databaseHelper: DatabaseHelper)

    fun inject(accountAdapter: AccountAdapter)
    fun inject(monthTransactionsPresenter: MonthTransactionsPresenter)
    fun inject(editAccountActivity: EditAccountActivity)
    fun inject(showAccountActivity: ShowAccountActivity)

    fun inject(billAdapter: BillAdapter)
    fun inject(billMonthlyResumeAdapter: BillMonthlyResumeAdapter)
    fun inject(editBillActivity: EditBillActivity)
    fun inject(showBillActivity: ShowBillActivity)

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

    fun inject(editSourceActivity: EditSourceActivity)
    fun inject(sourceAdapter: SourceAdapter)
    fun inject(showSourceActivity: ShowSourceActivity)

    fun inject(syncService: SyncService)
}
