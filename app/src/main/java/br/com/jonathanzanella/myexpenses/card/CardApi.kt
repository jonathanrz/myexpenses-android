package br.com.jonathanzanella.myexpenses.card

import android.util.Log
import br.com.jonathanzanella.myexpenses.MyApplication
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository
import br.com.jonathanzanella.myexpenses.server.Server
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import java.io.IOException

class CardApi : UnsyncModelApi<Card> {
    private val cardInterface: CardInterface by lazy {
        Server(MyApplication.getContext()).cardInterface()
    }
    private val cardRepository: CardRepository by lazy {
        CardRepository(expenseRepository)
    }
    private val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository()
    }

    override fun index(): List<Card> {
        val caller = cardInterface.index(greaterUpdatedAt())

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                return response.body()
            } else {
                Log.e(LOG_TAG, "Index request error: " + response.message())
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Index request error: " + e.message)
            e.printStackTrace()
        }

        return emptyList()
    }

    override fun save(model: UnsyncModel) {
        val card = model as Card
        val caller = if (StringUtils.isNotEmpty(card.serverId))
            cardInterface.update(card.serverId, card)
        else
            cardInterface.create(card)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                cardRepository.syncAndSave(response.body())
                Log.i(LOG_TAG, "Updated: " + card.getData())
            } else {
                Log.e(LOG_TAG, "Save request error: " + response.message() + " uuid: " + card.uuid)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Save request error: " + e.message + " uuid: " + card.uuid)
            e.printStackTrace()
        }

    }

    override fun syncAndSave(unsync: UnsyncModel) {
        if (unsync !is Card)
            throw UnsupportedOperationException("UnsyncModel is not a Card")
        cardRepository.syncAndSave(unsync)
    }

    override fun unsyncModels(): List<Card> {
        return cardRepository.unsync()
    }

    override fun greaterUpdatedAt(): Long {
        return cardRepository.greaterUpdatedAt()
    }

    companion object {
        private val LOG_TAG = CardApi::class.java.simpleName
    }
}