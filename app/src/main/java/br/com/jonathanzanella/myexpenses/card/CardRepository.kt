package br.com.jonathanzanella.myexpenses.card

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.database.Fields
import br.com.jonathanzanella.myexpenses.database.ModelRepository
import br.com.jonathanzanella.myexpenses.database.Repository
import br.com.jonathanzanella.myexpenses.database.Where
import br.com.jonathanzanella.myexpenses.expense.Expense
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.log.Log
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import java.util.*

open class CardRepository(private val repository: Repository<Card>, private val expenseRepository: ExpenseRepository) : ModelRepository<Card> {
    private val table = CardTable()

    @WorkerThread
    fun find(uuid: String): Card? {
        return repository.find(table, uuid)
    }

    @WorkerThread
    fun all(): List<Card> {
        return repository.query(table, Where(null).orderBy(Fields.NAME))
    }

    @WorkerThread
    fun creditCards(): List<Card> {
        return repository.query(table, Where(Fields.TYPE).eq(CardType.CREDIT.value))
    }

    @WorkerThread
    fun accountDebitCard(account: Account): Card? {
        return repository.querySingle(table,
                Where(Fields.TYPE)
                        .eq(CardType.DEBIT.value)
                        .and(Fields.ACCOUNT_UUID)
                        .eq(account.uuid!!))
    }

    @WorkerThread
    fun unsync(): List<Card> {
        return repository.unsync(table)
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return repository.greaterUpdatedAt(table)
    }

    @WorkerThread
    fun save(card: Card): ValidationResult {
        val result = validate(card)
        if (result.isValid) {
            if (card.id == 0L && card.uuid == null)
                card.uuid = UUID.randomUUID().toString()
            card.sync = false
            repository.saveAtDatabase(table, card)
        }
        return result
    }

    private fun validate(card: Card): ValidationResult {
        val result = ValidationResult()
        if (StringUtils.isEmpty(card.name))
            result.addError(ValidationError.NAME)
        if (card.type == null)
            result.addError(ValidationError.CARD_TYPE)
        if (card.account == null)
            result.addError(ValidationError.ACCOUNT)
        return result
    }

    @WorkerThread
    override fun syncAndSave(unsyncCard: Card): ValidationResult {
        val result = validate(unsyncCard)
        if (!result.isValid) {
            Log.warning("Card sync validation failed", unsyncCard.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val card = find(unsyncCard.uuid!!)
        if (card != null && card.id != unsyncCard.id) {
            if (card.updatedAt != unsyncCard.updatedAt)
                Log.warning("Card overwritten", unsyncCard.getData())
            unsyncCard.id = card.id
        }

        unsyncCard.sync = true
        repository.saveAtDatabase(table, unsyncCard)

        return result
    }

    @WorkerThread
    fun creditCardBills(card: Card, month: DateTime): List<Expense> {
        return expenseRepository.unpaidCardExpenses(month, card)
    }

    @WorkerThread
    fun getInvoiceValue(card: Card, month: DateTime): Int {
        var total = 0
        for (expense in creditCardBills(card, month))
            total += expense.value

        return total
    }
}