package br.com.jonathanzanella.myexpenses.expense

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardDataSource
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.database.DataSourceObserver
import br.com.jonathanzanella.myexpenses.helpers.firstDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.firstMillisOfDay
import br.com.jonathanzanella.myexpenses.helpers.lastDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.lastMillisOfDay
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter
import br.com.jonathanzanella.myexpenses.transaction.Transaction
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import io.reactivex.Flowable
import io.reactivex.Observable
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

interface ExpenseDataSource {
    fun all(): List<Expense>
    fun monthly(month: DateTime): List<Expense>
    fun expenses(period: WeeklyPagerAdapter.Period, card: Card? = null): List<Expense>
    fun unpaidCardExpenses(month: DateTime, card: Card): List<Expense>
    fun expensesForResumeScreen(date: DateTime): List<Expense>
    fun accountExpenses(account: Account, month: DateTime): Flowable<List<Transaction>>
    fun unsync(): List<Expense>
    fun creditCardBills(card: Card, month: DateTime): List<Expense>

    fun find(uuid: String): Expense?
    fun greaterUpdatedAt(): Long
    fun getInvoiceValue(card: Card, month: DateTime): Int

    fun save(expense: Expense): ValidationResult
    fun syncAndSave(unsync: Expense): ValidationResult

    fun registerDataSourceObserver(observer: DataSourceObserver)
}

class ExpenseRepository @Inject constructor(private val dao: ExpenseDao, val cardDataSource: CardDataSource): ExpenseDataSource {
    private val observers = ArrayList<WeakReference<DataSourceObserver>>()

    override fun registerDataSourceObserver(observer: DataSourceObserver) {
        observers.add(WeakReference(observer))
    }

    private fun refreshObservables() {
        observers.forEach { it.get()?.onDataChanged() }
    }

    @WorkerThread
    override fun all(): List<Expense> = dao.all().blockingFirst()

    @WorkerThread
    override fun monthly(month: DateTime): List<Expense> {
        val lastMonth = month.minusMonths(1)
        var initOfMonth = lastMonth.firstDayOfMonth()
        var endOfMonth = lastMonth.lastDayOfMonth()

        val expenses = dao.nextMonth(initOfMonth.millis, endOfMonth.millis).blockingFirst()

        initOfMonth = month.firstDayOfMonth()
        endOfMonth = month.lastDayOfMonth()

        expenses.addAll(dao.currentMonth(initOfMonth.millis, endOfMonth.millis).blockingFirst())

        return expenses
    }

    @WorkerThread
    override fun expenses(period: WeeklyPagerAdapter.Period, card: Card?): List<Expense> {
        val expenses = ArrayList<Expense>()

        if (period.init?.dayOfMonth == 1) {
            val date = period.init!!.firstDayOfMonth()
            val initOfMonth = date.minusMonths(1)
            val endOfMonth = initOfMonth.lastDayOfMonth()

            if (card != null)
                expenses.addAll(dao.overviewNextMonth(initOfMonth.millis, endOfMonth.millis, card.uuid!!).blockingFirst())
            else
                expenses.addAll(dao.overviewNextMonth(initOfMonth.millis, endOfMonth.millis).blockingFirst())
        }

        val init = period.init!!.firstMillisOfDay()
        val end = period.end!!.lastMillisOfDay()

        if (card != null)
            expenses.addAll(dao.overviewCurrentMonth(init.millis, end.millis, card.uuid!!).blockingFirst())
        else
            expenses.addAll(dao.overviewCurrentMonth(init.millis, end.millis).blockingFirst())

        return expenses
    }

    @WorkerThread
    override fun unpaidCardExpenses(month: DateTime, card: Card): List<Expense> {
        val expenses = ArrayList<Expense>()
        val lastMonth = month.minusMonths(1)

        expenses.addAll(dao.unchargedNextMonth(lastMonth.firstDayOfMonth().millis, lastMonth.lastDayOfMonth().millis, card.uuid!!).blockingFirst())
        expenses.addAll(dao.unchargedCurrentMonth(month.firstDayOfMonth().millis, month.lastDayOfMonth().millis, card.uuid!!).blockingFirst())

        return expenses
    }

    @WorkerThread
    override fun expensesForResumeScreen(date: DateTime): List<Expense> {
        val lastMonth = date.minusMonths(1)
        var initOfMonth = lastMonth.firstDayOfMonth()
        var endOfMonth = lastMonth.lastDayOfMonth()

        val expenses = dao.resumeNextMonth(initOfMonth.millis, endOfMonth.millis, ChargeableType.CREDIT_CARD.name).blockingFirst()

        initOfMonth = date.firstDayOfMonth()
        endOfMonth = date.lastDayOfMonth()

        expenses.addAll(dao.resumeCurrentMonth(initOfMonth.millis, endOfMonth.millis, ChargeableType.CREDIT_CARD.name).blockingFirst())

        val creditCardMonth = date.minusMonths(1)
        val cards = cardDataSource.creditCards()
        for (card in cards) {
            val total = getInvoiceValue(card, creditCardMonth)
            if (total == 0)
                continue

            val expense = Expense()
            expense.setChargeable(card)
            expense.name = App.getContext().getString(R.string.invoice)
            expense.setDate(creditCardMonth)
            expense.value = total
            expense.creditCard = card
            expenses.add(expense)
        }

        return expenses
    }

    override fun accountExpenses(account: Account, month: DateTime): Flowable<List<Transaction>> {
        val card = cardDataSource.accountDebitCard(account)

        return dao.currentMonth(month.firstDayOfMonth().millis, month.lastDayOfMonth().millis, account.uuid!!)
                .mergeWith {
                    if(card != null)
                        dao.nextMonth(month.firstDayOfMonth().millis, month.lastDayOfMonth().millis, card.uuid!!)
                    else
                        Flowable.just(emptyList<Expense>())
                }
                .mergeWith {
                    Observable.fromCallable {
                        val expenses = ArrayList<Expense>()
                        if (account.accountToPayCreditCard) {
                            val creditCardMonth = month.minusMonths(1)
                            val cards = cardDataSource.creditCards()
                            for (creditCard in cards) {
                                val total = getInvoiceValue(creditCard, creditCardMonth)
                                if (total == 0)
                                    continue

                                val expense = Expense()
                                expense.setChargeable(creditCard)
                                expense.name = App.getContext().getString(R.string.invoice) + " " + creditCard.name
                                expense.setDate(creditCardMonth.plusMonths(1))
                                expense.value = total
                                expense.creditCard = creditCard
                                expenses.add(expense)
                            }
                        }
                        expenses
                    }
                }.map {
                    it.map { expense -> expense as Transaction }
                }
    }

    @WorkerThread
    override fun unsync(): List<Expense> {
        return dao.unsync().blockingFirst()
    }

    @WorkerThread
    override fun creditCardBills(card: Card, month: DateTime) = unpaidCardExpenses(month, card)

    @WorkerThread
    override fun find(uuid: String): Expense? {
        return dao.find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    override fun greaterUpdatedAt(): Long {
        return dao.greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    override fun getInvoiceValue(card: Card, month: DateTime) = creditCardBills(card, month).sumBy { it.value }

    @WorkerThread
    override fun save(expense: Expense): ValidationResult {
        val result = validate(expense)
        if (result.isValid) {
            if (expense.id == 0L && expense.uuid == null)
                expense.uuid = UUID.randomUUID().toString()
            expense.sync = false
            expense.id = dao.saveAtDatabase(expense)

            refreshObservables()
        }
        return result
    }

    private fun validate(expense: Expense): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(expense.name))
            result.addError(ValidationError.NAME)
        if (expense.value == 0)
            result.addError(ValidationError.AMOUNT)
        if (!expense.dateIsPresent())
            result.addError(ValidationError.DATE)
        if (expense.chargeableFromCache == null)
            result.addError(ValidationError.CHARGEABLE)
        return result
    }

    @WorkerThread
    override fun syncAndSave(unsync: Expense): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Timber.tag("Expense validation fail")
                    .w( unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val expense = find(unsync.uuid!!)
        if (expense != null && expense.id != unsync.id) {
            if (expense.updatedAt != unsync.updatedAt)
                Timber.tag("Expense overwritten")
                        .w(unsync.getData())
            unsync.id = expense.id
        }

        unsync.sync = true
        unsync.id = dao.saveAtDatabase(unsync)

        refreshObservables()

        return result
    }
}
