package br.com.jonathanzanella.myexpenses.injection

import br.com.jonathanzanella.myexpenses.account.AccountRepositoryTest
import br.com.jonathanzanella.myexpenses.account.AccountViewTest
import br.com.jonathanzanella.myexpenses.account.EditAccountTest
import br.com.jonathanzanella.myexpenses.account.ShowAccountActivityTest
import br.com.jonathanzanella.myexpenses.bill.BillRepositoryTest
import br.com.jonathanzanella.myexpenses.card.CardRepositoryTest
import br.com.jonathanzanella.myexpenses.card.EditCardTest
import br.com.jonathanzanella.myexpenses.card.ShowCardActivityTest
import br.com.jonathanzanella.myexpenses.expense.*
import br.com.jonathanzanella.myexpenses.receipt.EditReceiptTest
import br.com.jonathanzanella.myexpenses.receipt.ReceiptRepositoryTest
import br.com.jonathanzanella.myexpenses.receipt.ReceiptsInPeriodTest
import br.com.jonathanzanella.myexpenses.receipt.ReceiptsViewTest
import br.com.jonathanzanella.myexpenses.resume.ShowDetailScreenTest
import br.com.jonathanzanella.myexpenses.source.ListSourceActivityTest
import br.com.jonathanzanella.myexpenses.source.ShowSourceActivityTest
import br.com.jonathanzanella.myexpenses.source.SourceRepositoryTest
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