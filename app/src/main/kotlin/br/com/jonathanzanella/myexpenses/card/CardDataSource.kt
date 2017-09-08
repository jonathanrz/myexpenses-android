package br.com.jonathanzanella.myexpenses.card

import android.support.annotation.WorkerThread
import br.com.jonathanzanella.myexpenses.account.Account
import br.com.jonathanzanella.myexpenses.validations.ValidationError
import br.com.jonathanzanella.myexpenses.validations.ValidationResult
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.util.*
import javax.inject.Inject

interface CardDataSource {
    fun all(): List<Card>
    fun unsync(): List<Card>
    fun creditCards(): List<Card>

    fun find(uuid: String): Card?
    fun accountDebitCard(account: Account): Card?
    fun greaterUpdatedAt(): Long

    fun save(card: Card): ValidationResult
    fun syncAndSave(unsync: Card): ValidationResult
}

class CardRepository @Inject constructor(val dao: CardDao): CardDataSource {

    @WorkerThread
    override fun all(): List<Card> {
        return dao.all().blockingFirst()
    }

    @WorkerThread
    override fun unsync(): List<Card> {
        return dao.unsync().blockingFirst()
    }

    @WorkerThread
    override fun creditCards(): List<Card> {
        return dao.cards(CardType.CREDIT.value).blockingFirst()
    }

    @WorkerThread
    override fun find(uuid: String): Card? {
        return dao.find(uuid).blockingFirst().firstOrNull()
    }

    @WorkerThread
    override fun accountDebitCard(account: Account): Card? {
        return dao.accountCard(CardType.DEBIT.value, account.uuid!!).blockingFirst().firstOrNull()
    }

    @WorkerThread
    override fun greaterUpdatedAt(): Long {
        return dao.greaterUpdatedAt().blockingFirst().firstOrNull()?.updatedAt ?: 0L
    }

    @WorkerThread
    override fun save(card: Card): ValidationResult {
        val result = validate(card)
        if (result.isValid) {
            if (card.id == 0L && card.uuid == null)
                card.uuid = UUID.randomUUID().toString()
            card.sync = false
            card.id = dao.saveAtDatabase(card)
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
    override fun syncAndSave(unsync: Card): ValidationResult {
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
        unsync.id = dao.saveAtDatabase(unsync)

        return result
    }
}
