package br.com.jonathanzanella.myexpenses.injection

import br.com.jonathanzanella.myexpenses.ui.receipt.EditReceiptTest
import br.com.jonathanzanella.myexpenses.ui.receipt.ReceiptRepositoryTest
import br.com.jonathanzanella.myexpenses.ui.receipt.ReceiptsInPeriodTest
import br.com.jonathanzanella.myexpenses.ui.receipt.ReceiptsViewTest
import br.com.jonathanzanella.myexpenses.ui.resume.ShowDetailScreenTest
import br.com.jonathanzanella.myexpenses.ui.source.ListSourceActivityTest
import br.com.jonathanzanella.myexpenses.ui.source.ShowSourceActivityTest
import br.com.jonathanzanella.myexpenses.ui.source.SourceRepositoryTest
import br.com.jonathanzanella.myexpenses.ui.account.AccountRepositoryTest
import br.com.jonathanzanella.myexpenses.ui.account.AccountViewTest
import br.com.jonathanzanella.myexpenses.ui.account.EditAccountTest
import br.com.jonathanzanella.myexpenses.ui.account.ShowAccountActivityTest
import br.com.jonathanzanella.myexpenses.ui.card.CardRepositoryTest
import br.com.jonathanzanella.myexpenses.ui.card.EditCardTest
import br.com.jonathanzanella.myexpenses.ui.card.ShowCardActivityTest
import br.com.jonathanzanella.myexpenses.ui.expense.*
import dagger.Component
import javax.inject.Singleton

//Idea based in https://medium.com/@fabioCollini/android-testing-using-dagger-2-mockito-and-a-custom-junit-rule-c8487ed01b56

@Singleton
@Component(modules = arrayOf(AppModule::class, DatabaseModule::class, RepositoryModule::class, PresenterModule::class, ServerModule::class, AccountModule::class, BillModule::class))
interface TestComponent: AppComponent {
    fun inject(accountRepositoryTest: AccountRepositoryTest)
    fun inject(showAccountActivityTest: ShowAccountActivityTest)
    fun inject(showCardActivityTest: ShowCardActivityTest)
    fun inject(showExpenseActivityTest: ShowExpenseActivityTest)
    fun inject(editExpenseTest: EditExpenseTest)
    fun inject(expensesInPeriodTest: ExpensesInPeriodTest)
    fun inject(expensesViewTest: ExpensesViewTest)
    fun inject(expenseRepositoryTest: ExpenseRepositoryTest)
    fun inject(accountViewTest: AccountViewTest)
    fun inject(editAccountTest: EditAccountTest)
    fun inject(sourceRepositoryTest: SourceRepositoryTest)
    fun inject(listSourceActivityTest: ListSourceActivityTest)
    fun inject(showSourceActivityTest: ShowSourceActivityTest)
    fun inject(showDetailScreenTest: ShowDetailScreenTest)
    fun inject(cardRepositoryTest: CardRepositoryTest)
    fun inject(editCardTest: EditCardTest)
    fun inject(receiptRepositoryTest: ReceiptRepositoryTest)
    fun inject(receiptsInPeriodTest: ReceiptsInPeriodTest)
    fun inject(editReceiptTest: EditReceiptTest)
    fun inject(receiptsViewTest: ReceiptsViewTest)
}