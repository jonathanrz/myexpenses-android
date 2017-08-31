package br.com.jonathanzanella.myexpenses.expense

import android.support.annotation.WorkerThread
import android.util.Log
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardRepository
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.helpers.DateHelper
import br.com.jonathanzanella.myexpenses.helpers.DateHelper.firstDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.DateHelper.lastDayOfMonth
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

open class ExpenseRepository() {
    private val cardRepository: CardRepository by lazy {
        CardRepository(this)
    }

    @WorkerThread
    fun find(uuid: String): Expense? {
        return MyApplication.database.expenseDao().find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    fun all(): List<Expense> {
        return MyApplication.database.expenseDao().all().blockingFirst()
    }

    @WorkerThread
    fun monthly(month: DateTime): List<Expense> {
        val lastMonth = month.minusMonths(1)
        var initOfMonth = firstDayOfMonth(lastMonth)
        var endOfMonth = lastDayOfMonth(lastMonth)

        val expenses = MyApplication.database.expenseDao().nextMonth(initOfMonth.millis, endOfMonth.millis).blockingFirst()

        initOfMonth = firstDayOfMonth(month)
        endOfMonth = lastDayOfMonth(month)

        expenses.addAll(MyApplication.database.expenseDao().currentMonth(initOfMonth.millis, endOfMonth.millis).blockingFirst())

        return expenses
    }

    fun expenses(period: WeeklyPagerAdapter.Period): List<Expense> {
        return expenses(period, null)
    }

    @WorkerThread
    fun expenses(period: WeeklyPagerAdapter.Period, card: Card?): List<Expense> {
        val expenses = ArrayList<Expense>()

        if (period.init?.dayOfMonth == 1) {
            val date = DateHelper.firstDayOfMonth(period.init!!)
            val initOfMonth = date.minusMonths(1)
            val endOfMonth = DateHelper.lastDayOfMonth(initOfMonth)

            if (card != null)
                expenses.addAll(MyApplication.database.expenseDao().overviewNextMonth(initOfMonth.millis, endOfMonth.millis, card.uuid!!).blockingFirst())
            else
                expenses.addAll(MyApplication.database.expenseDao().overviewNextMonth(initOfMonth.millis, endOfMonth.millis).blockingFirst())
        }

        val init = DateHelper.firstMillisOfDay(period.init!!)
        val end = DateHelper.lastMillisOfDay(period.end!!)

        if (card != null)
            expenses.addAll(MyApplication.database.expenseDao().overviewCurrentMonth(init.millis, end.millis, card.uuid!!).blockingFirst())
        else
            expenses.addAll(MyApplication.database.expenseDao().overviewCurrentMonth(init.millis, end.millis).blockingFirst())

        return expenses
    }

    @WorkerThread
    fun unpaidCardExpenses(month: DateTime, card: Card): List<Expense> {
        val expenses = ArrayList<Expense>()
        val lastMonth = month.minusMonths(1)

        expenses.addAll(MyApplication.database.expenseDao().unchargedNextMonth(firstDayOfMonth(lastMonth).millis, lastDayOfMonth(lastMonth).millis, card.uuid!!).blockingFirst())
        expenses.addAll(MyApplication.database.expenseDao().unchargedCurrentMonth(firstDayOfMonth(lastMonth).millis, lastDayOfMonth(lastMonth).millis, card.uuid!!).blockingFirst())

        return expenses
    }

    @WorkerThread
    fun expensesForResumeScreen(date: DateTime): List<Expense> {
        val lastMonth = date.minusMonths(1)
        var initOfMonth = DateHelper.firstDayOfMonth(lastMonth)
        var endOfMonth = DateHelper.lastDayOfMonth(lastMonth)

        val expenses = MyApplication.database.expenseDao().resumeNextMonth(initOfMonth.millis, endOfMonth.millis, ChargeableType.CREDIT_CARD.name).blockingFirst()

        initOfMonth = DateHelper.firstDayOfMonth(date)
        endOfMonth = DateHelper.lastDayOfMonth(date)

        expenses.addAll(MyApplication.database.expenseDao().resumeCurrentMonth(initOfMonth.millis, endOfMonth.millis, ChargeableType.CREDIT_CARD.name).blockingFirst())

        val creditCardMonth = date.minusMonths(1)
        val cards = cardRepository.creditCards()
        for (card in cards) {
            val total = cardRepository.getInvoiceValue(card, creditCardMonth)
            if (total == 0)
                continue

            val expense = Expense()
            expense.setChargeable(card)
            expense.name = MyApplication.getContext().getString(R.string.invoice)
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
        var initOfMonth = DateHelper.firstDayOfMonth(lastMonth)
        var endOfMonth = DateHelper.lastDayOfMonth(lastMonth)

        val card = cardRepository.accountDebitCard(account)

        val expenses = MyApplication.database.expenseDao().nextMonth(initOfMonth.millis, endOfMonth.millis, account.uuid!!).blockingFirst()

        if (card != null)
            expenses.addAll(MyApplication.database.expenseDao().nextMonth(initOfMonth.millis, endOfMonth.millis, card.uuid!!).blockingFirst())

        initOfMonth = firstDayOfMonth(month)
        endOfMonth = lastDayOfMonth(month)

        expenses.addAll(MyApplication.database.expenseDao().currentMonth(initOfMonth.millis, endOfMonth.millis, account.uuid!!).blockingFirst())

        if (card != null)
            expenses.addAll(MyApplication.database.expenseDao().currentMonth(initOfMonth.millis, endOfMonth.millis, card.uuid!!).blockingFirst())

        if (account.isAccountToPayCreditCard) {
            val creditCardMonth = month.minusMonths(1)
            val cards = cardRepository.creditCards()
            for (creditCard in cards) {
                val total = cardRepository.getInvoiceValue(creditCard, creditCardMonth)
                if (total == 0)
                    continue

                val expense = Expense()
                expense.setChargeable(creditCard)
                expense.name = MyApplication.getContext().getString(R.string.invoice) + " " + creditCard.name
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
        return MyApplication.database.expenseDao().greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    fun unsync(): List<Expense> {
        return MyApplication.database.expenseDao().unsync().blockingFirst()
    }

    @WorkerThread
    fun save(expense: Expense): ValidationResult {
        val result = validate(expense)
        if (result.isValid) {
            if (expense.id == 0L && expense.uuid == null)
                expense.uuid = UUID.randomUUID().toString()
            expense.sync = false
            expense.id = MyApplication.database.expenseDao().saveAtDatabase(expense)
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
            Log.w("Expense validation fail", unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val expense = find(unsync.uuid!!)
        if (expense != null && expense.id != unsync.id) {
            if (expense.updatedAt != unsync.updatedAt)
                Log.w("Expense overwritten", unsync.getData())
            unsync.id = expense.id
        }

        unsync.sync = true
        unsync.id = MyApplication.database.expenseDao().saveAtDatabase(unsync)

        return result
    }
}