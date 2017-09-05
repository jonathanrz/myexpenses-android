package br.com.jonathanzanella.myexpenses.card

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*

open class CardRepository(private val expenseRepository: ExpenseRepository) {

    @WorkerThread
    fun find(uuid: String): Card? {
        return App.database.cardDao().find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    fun all(): List<Card> {
        return App.database.cardDao().all().blockingFirst()
    }

    @WorkerThread
    fun unsync(): List<Card> {
        return App.database.cardDao().unsync().blockingFirst()
    }

    @WorkerThread
    fun creditCards(): List<Card> {
        return App.database.cardDao().cards(CardType.CREDIT.value).blockingFirst()
    }

    @WorkerThread
    fun accountDebitCard(account: Account): Card? {
        return App.database.cardDao().accountCard(CardType.DEBIT.value, account.uuid!!).blockingFirst().firstOrNull()
    }

    @WorkerThread
    fun greaterUpdatedAt(): Long {
        return App.database.cardDao().greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    fun save(card: Card): ValidationResult {
        val result = validate(card)
        if (result.isValid) {
            if (card.id == 0L && card.uuid == null)
                card.uuid = UUID.randomUUID().toString()
            card.sync = false
            card.id = App.database.cardDao().saveAtDatabase(card)
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
    fun syncAndSave(unsync: Card): ValidationResult {
        val result = validate(unsync)
        if (!result.isValid) {
            Timber.tag("Card sync valida failed")
                    .w(unsync.getData() + "\nerrors: " + result.errorsAsString)
            return result
        }

        val card = find(unsync.uuid!!)
        if (card != null && card.id != unsync.id) {
            if (card.updatedAt != unsync.updatedAt)
                Timber.tag("Card overwritten").w(unsync.getData())
            unsync.id = card.id
        }

        unsync.sync = true
        unsync.id = App.database.cardDao().saveAtDatabase(unsync)

        return result
    }

    @WorkerThread
    fun creditCardBills(card: Card, month: DateTime) =
        expenseRepository.unpaidCardExpenses(month, card)

    @WorkerThread
    fun getInvoiceValue(card: Card, month: DateTime) =
        creditCardBills(card, month).sumBy { it.value }
}
