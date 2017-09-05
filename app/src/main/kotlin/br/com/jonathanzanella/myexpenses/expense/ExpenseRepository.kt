package br.com.jonathanzanella.myexpenses.expense

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardRepository
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.helpers.firstDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.firstMillisOfDay
import br.com.jonathanzanella.myexpenses.helpers.lastDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.lastMillisOfDay
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*

open class ExpenseRepository(private val dao: ExpenseDao = App.database.expenseDao()) {
    private val cardRepository: CardRepository by lazy {
        CardRepository(this)
    }

    @WorkerThread
    fun find(uuid: String): Expense? {
        return dao.find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    fun all(): List<Expense> {
        return dao.all().blockingFirst()
    }

    @WorkerThread
    fun monthly(month: DateTime): List<Expense> {
        val lastMonth = month.minusMonths(1)
        var initOfMonth = lastMonth.firstDayOfMonth()
        var endOfMonth = lastMonth.lastDayOfMonth()

        val expenses = dao.nextMonth(initOfMonth.millis, endOfMonth.millis).blockingFirst()

        initOfMonth = month.firstDayOfMonth()
        endOfMonth = month.lastDayOfMonth()

        expenses.addAll(dao.currentMonth(initOfMonth.millis, endOfMonth.millis).blockingFirst())

        return expenses
    }

    fun expenses(period: WeeklyPagerAdapter.Period): List<Expense> {
        return expenses(period, null)
    }

    @WorkerThread
    fun expenses(period: WeeklyPagerAdapter.Period, card: Card?): List<Expense> {
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
    fun unpaidCardExpenses(month: DateTime, card: Card): List<Expense> {
        val expenses = ArrayList<Expense>()
        val lastMonth = month.minusMonths(1)

        expenses.addAll(dao.unchargedNextMonth(lastMonth.firstDayOfMonth().millis, lastMonth.lastDayOfMonth().millis, card.uuid!!).blockingFirst())
        expenses.addAll(dao.unchargedCurrentMonth(month.firstDayOfMonth().millis, month.lastDayOfMonth().millis, card.uuid!!).blockingFirst())

        return expenses
    }

    @WorkerThread
    fun expensesForResumeScreen(date: DateTime): List<Expense> {
        val lastMonth = date.minusMonths(1)
        var initOfMonth = lastMonth.firstDayOfMonth()
        var endOfMonth = lastMonth.lastDayOfMonth()

        val expenses = dao.resumeNextMonth(initOfMonth.millis, endOfMonth.millis, ChargeableType.CREDIT_CARD.name).blockingFirst()

        initOfMonth = date.firstDayOfMonth()
        endOfMonth = date.lastDayOfMonth()

        expenses.addAll(dao.resumeCurrentMonth(initOfMonth.millis, endOfMonth.millis, ChargeableType.CREDIT_CARD.name).blockingFirst())

        val creditCardMonth = date.minusMonths(1)
        val cards = cardRepository.creditCards()
        for (card in cards) {
            val total = cardRepository.getInvoiceValue(card, creditCardMonth)
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

    @WorkerThread
    fun accountExpenses(account: Account, month: DateTime): List<Expense> {
        val lastMonth = month.minusMonths(1)
        var initOfMonth = lastMonth.firstDayOfMonth()
        var endOfMonth = lastMonth.lastDayOfMonth()

        val card = cardRepository.accountDebitCard(account)

        val expenses = dao.nextMonth(initOfMonth.millis, endOfMonth.millis, account.uuid!!).blockingFirst()

        if (card != null)
            expenses.addAll(dao.nextMonth(initOfMonth.millis, endOfMonth.millis, card.uuid!!).blockingFirst())

        initOfMonth = month.firstDayOfMonth()
        endOfMonth = month.lastDayOfMonth()

        expenses.addAll(dao.currentMonth(initOfMonth.millis, endOfMonth.millis, account.uuid!!).blockingFirst())

        if (card != null)
            expenses.addAll(dao.currentMonth(initOfMonth.millis, endOfMonth.millis, card.uuid!!).blockingFirst())

        if (account.accountToPayCreditCard) {
            val creditCardMonth = month.minusMonths(1)
            val cards = cardRepository.creditCards()
            for (creditCard in cards) {
                val total = cardRepository.getInvoiceValue(creditCard, creditCardMonth)
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

        Collections.sort(expenses, Comparator<Expense> { lhs, rhs ->
            if (lhs.getDate().isAfter(rhs.getDate()))
                return@Comparator 1
            -1
        })

        return expenses
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return dao.greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    fun unsync(): List<Expense> {
        return dao.unsync().blockingFirst()
    }

    @WorkerThread
    fun save(expense: Expense): ValidationResult {
        val result = validate(expense)
        if (result.isValid) {
            if (expense.id == 0L && expense.uuid == null)
                expense.uuid = UUID.randomUUID().toString()
            expense.sync = false
            expense.id = dao.saveAtDatabase(expense)
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
    fun syncAndSave(unsync: Expense): ValidationResult {
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

        return result
    }
}
