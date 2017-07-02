package br.com.jonathanzanella.myexpenses.expense

import android.support.annotation.WorkerThread

import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.UUID

import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.R
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.card.Card
import br.com.jonathanzanella.myexpenses.card.CardRepository
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.ModelRepository
import br.com.jonathanzanella.myexpenses.database.Repository
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.database.Where
import br.com.jonathanzanella.myexpenses.helpers.DateHelper
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.overview.WeeklyPagerAdapter
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult

import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType.ACCOUNT
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType.DEBIT_CARD
import br.com.jonathanzanella.myexpenses.helpers.DateHelper.firstDayOfMonth
import br.com.jonathanzanella.myexpenses.helpers.DateHelper.lastDayOfMonth

class ExpenseRepository(private val repository: Repository<Expense>) : ModelRepository<Expense> {
    private val table = ExpenseTable()
    private val cardRepository: CardRepository by lazy {
        CardRepository(RepositoryImpl<Card>(MyApplication.getContext()), this)
    }

    @WorkerThread
    fun find(uuid: String): Expense? {
        return repository.find(table, uuid)
    }

    @WorkerThread
    fun all(): List<Expense> {
        return repository.query(table, Where(null).orderBy(Fields.DATE))
    }

    @WorkerThread
    fun monthly(month: DateTime): List<Expense> {
        val lastMonth = month.minusMonths(1)
        var initOfMonth = firstDayOfMonth(lastMonth)
        var endOfMonth = lastDayOfMonth(lastMonth)

        val expenses = repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
                .and(Fields.CHARGE_NEXT_MONTH).eq(true)
                .orderBy(Fields.DATE)) as MutableList<Expense>

        initOfMonth = firstDayOfMonth(month)
        endOfMonth = lastDayOfMonth(month)

        expenses.addAll(repository.query(table, queryBetween(initOfMonth, endOfMonth)
                .and(Fields.REMOVED).eq(false)
                .and(Fields.CHARGE_NEXT_MONTH).eq(false)
                .orderBy(Fields.DATE)))

        return expenses
    }

    fun expenses(period: WeeklyPagerAdapter.Period): List<Expense> {
        return expenses(period, null)
    }

    @WorkerThread
    fun expenses(period: WeeklyPagerAdapter.Period, card: Card?): List<Expense> {
        val expenses = ArrayList<Expense>()

        if (period.init.dayOfMonth == 1) {
            val date = DateHelper.firstDayOfMonth(period.init)
            val initOfMonth = date.minusMonths(1)
            val endOfMonth = DateHelper.lastDayOfMonth(initOfMonth)

            var where = queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
                    .and(Fields.CHARGE_NEXT_MONTH).eq(true)
                    .and(Fields.IGNORE_IN_OVERVIEW).eq(false)
                    .orderBy(Fields.DATE)
            if (card != null)
                where = where.and(Fields.CHARGEABLE_UUID).eq(card.uuid!!)
            expenses.addAll(repository.query(table, where))
        }

        val init = DateHelper.firstMillisOfDay(period.init)
        val end = DateHelper.lastMillisOfDay(period.end)

        var where = queryBetweenUserDataAndNotRemoved(init, end)
                .and(Fields.CHARGE_NEXT_MONTH).eq(false)
                .and(Fields.IGNORE_IN_OVERVIEW).eq(false)
                .orderBy(Fields.DATE)
        if (card != null)
            where = where.and(Fields.CHARGEABLE_UUID).eq(card.uuid!!)
        expenses.addAll(repository.query(table, where))

        return expenses
    }

    @WorkerThread
    fun unpaidCardExpenses(month: DateTime, card: Card): List<Expense> {
        val expenses = ArrayList<Expense>()
        val lastMonth = month.minusMonths(1)

        var where = queryBetweenUserDataAndNotRemoved(firstDayOfMonth(lastMonth), lastDayOfMonth(lastMonth))
                .and(Fields.CHARGEABLE_TYPE).eq(card.chargeableType.name)
                .and(Fields.CHARGEABLE_UUID).eq(card.uuid!!)
                .and(Fields.CHARGE_NEXT_MONTH).eq(true)
                .and(Fields.CHARGED).eq(false)
                .orderBy(Fields.DATE)
        expenses.addAll(repository.query(table, where))

        where = queryBetweenUserDataAndNotRemoved(firstDayOfMonth(month), lastDayOfMonth(month))
                .and(Fields.CHARGEABLE_TYPE).eq(card.chargeableType.name)
                .and(Fields.CHARGEABLE_UUID).eq(card.uuid!!)
                .and(Fields.CHARGE_NEXT_MONTH).eq(false)
                .and(Fields.CHARGED).eq(false)
                .orderBy(Fields.DATE)
        expenses.addAll(repository.query(table, where))

        return expenses
    }

    @WorkerThread
    fun expensesForResumeScreen(date: DateTime): List<Expense> {
        val lastMonth = date.minusMonths(1)
        var initOfMonth = DateHelper.firstDayOfMonth(lastMonth)
        var endOfMonth = DateHelper.lastDayOfMonth(lastMonth)

        val expenses = repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
                .and(Fields.CHARGEABLE_TYPE).notEq(ChargeableType.CREDIT_CARD.name)
                .and(Fields.CHARGE_NEXT_MONTH).eq(true)
                .and(Fields.IGNORE_IN_RESUME).eq(false)
                .orderBy(Fields.DATE)) as MutableList<Expense>

        initOfMonth = DateHelper.firstDayOfMonth(date)
        endOfMonth = DateHelper.lastDayOfMonth(date)

        expenses.addAll(repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
                .and(Fields.CHARGEABLE_TYPE).notEq(ChargeableType.CREDIT_CARD.name)
                .and(Fields.CHARGE_NEXT_MONTH).eq(false)
                .and(Fields.IGNORE_IN_RESUME).eq(false)
                .orderBy(Fields.DATE)))

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

        val expenses = repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
                .and(Fields.CHARGEABLE_TYPE).eq(ACCOUNT.name)
                .and(Fields.CHARGEABLE_UUID).eq(account.uuid!!)
                .and(Fields.CHARGE_NEXT_MONTH).eq(true)
                .orderBy(Fields.DATE)) as MutableList<Expense>

        if (card != null) {
            expenses.addAll(repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
                    .and(Fields.CHARGEABLE_TYPE).eq(DEBIT_CARD.name)
                    .and(Fields.CHARGEABLE_UUID).eq(card.uuid!!)
                    .and(Fields.CHARGE_NEXT_MONTH).eq(true)
                    .orderBy(Fields.DATE)))
        }

        initOfMonth = firstDayOfMonth(month)
        endOfMonth = lastDayOfMonth(month)

        expenses.addAll(repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
                .and(Fields.CHARGEABLE_TYPE).eq(ACCOUNT.name)
                .and(Fields.CHARGEABLE_UUID).eq(account.uuid!!)
                .and(Fields.CHARGE_NEXT_MONTH).eq(false)
                .orderBy(Fields.DATE)))

        if (card != null) {
            expenses.addAll(repository.query(table, queryBetweenUserDataAndNotRemoved(initOfMonth, endOfMonth)
                    .and(Fields.CHARGEABLE_TYPE).eq(DEBIT_CARD.name)
                    .and(Fields.CHARGEABLE_UUID).eq(card.uuid!!)
                    .and(Fields.CHARGE_NEXT_MONTH).eq(false)
                    .orderBy(Fields.DATE)))
        }

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
        return repository.greaterUpdatedAt(table)
    }

    @WorkerThread
    fun unsync(): List<Expense> {
        return repository.unsync(table)
    }

    private fun queryBetween(init: DateTime, end: DateTime): Where {
        return Where(Fields.DATE).greaterThanOrEq(init.millis)
                .and(Fields.DATE).lessThanOrEq(end.millis)
    }

    private fun queryBetweenUserDataAndNotRemoved(init: DateTime, end: DateTime): Where {
        return queryBetween(init, end)
                .and(Fields.REMOVED).eq(false)
    }

    @WorkerThread
    fun save(expense: Expense): ValidationResult {
        val result = validate(expense)
        if (result.isValid) {
            if (expense.id == 0L && expense.uuid == null)
                expense.uuid = UUID.randomUUID().toString()
            expense.sync = false
            repository.saveAtDatabase(table, expense)
        }
        return result
    }

    private fun validate(expense: Expense): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(expense.name))
            result.addError(ValidationError.NAME)
        if (expense.value == 0)
            result.addError(ValidationError.AMOUNT)
        if (expense.getDate() == null)
            result.addError(ValidationError.DATE)
        if (expense.chargeableFromCache == null)
            result.addError(ValidationError.CHARGEABLE)
        return result
    }

    @WorkerThread
    override fun syncAndSave(unsync: Expense): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Log.warning("Expense sync validation failed", unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val expense = find(unsync.uuid!!)
        if (expense != null && expense.id != unsync.id) {
            if (expense.updatedAt != unsync.updatedAt)
                Log.warning("Expense overwritten", unsync.getData())
            unsync.id = expense.id
        }

        unsync.sync = true
        repository.saveAtDatabase(table, unsync)

        return result
    }
}