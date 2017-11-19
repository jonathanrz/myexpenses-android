package br.com.jonathanzanella.myexpenses.injection

import br.com.jonathanzanella.myexpenses.ui.account.AccountRepositoryTest
import br.com.jonathanzanella.myexpenses.ui.card.CardRepositoryTest
import br.com.jonathanzanella.myexpenses.ui.card.EditCardTest
import br.com.jonathanzanella.myexpenses.ui.card.ShowCardActivityTest
import br.com.jonathanzanella.myexpenses.ui.expense.EditExpenseTest
import br.com.jonathanzanella.myexpenses.ui.expense.ExpenseRepositoryTest
import br.com.jonathanzanella.myexpenses.ui.expense.ExpensesInPeriodTest
import br.com.jonathanzanella.myexpenses.ui.expense.ExpensesViewTest
import br.com.jonathanzanella.myexpenses.ui.receipt.ReceiptRepositoryTest
import br.com.jonathanzanella.myexpenses.ui.receipt.ReceiptsInPeriodTest
import br.com.jonathanzanella.myexpenses.ui.source.ListSourceActivityTest
import br.com.jonathanzanella.myexpenses.ui.source.SourceRepositoryTest
import dagger.Component
import javax.inject.Singleton

//Idea based in https://medium.com/@fabioCollini/android-testing-using-dagger-2-mockito-and-a-custom-junit-rule-c8487ed01b56

@Singleton
@Component(modules = arrayOf(AppModule::class, DatabaseModule::class, RepositoryModule::class, PresenterModule::class, ServerModule::class, AccountModule::class, BillModule::class))
interface TestComponent: AppComponent {
    fun inject(showCardActivityTest: ShowCardActivityTest)
    fun inject(editExpenseTest: EditExpenseTest)
    fun inject(expensesInPeriodTest: ExpensesInPeriodTest)
    fun inject(expensesViewTest: ExpensesViewTest)
    fun inject(expenseRepositoryTest: ExpenseRepositoryTest)
    fun inject(sourceRepositoryTest: SourceRepositoryTest)
    fun inject(listSourceActivityTest: ListSourceActivityTest)
    fun inject(cardRepositoryTest: CardRepositoryTest)
    fun inject(editCardTest: EditCardTest)
    fun inject(receiptRepositoryTest: ReceiptRepositoryTest)
    fun inject(receiptsInPeriodTest: ReceiptsInPeriodTest)
}