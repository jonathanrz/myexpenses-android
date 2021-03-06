package br.com.jonathanzanella.myexpenses.card

import br.com.jonathanzanella.myexpenses.sync.UnsyncModel
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class CardApi @Inject constructor(private val cardInterface: CardInterface,
                                  private val cardRepository: CardRepository): UnsyncModelApi<Card> {

    override fun index(): List<Card> {
        val lastUpdatedAt = greaterUpdatedAt()
        Timber.tag("CardApi.index with lastUpdatedAt: $lastUpdatedAt")
        val caller = cardInterface.index(lastUpdatedAt)

        try {
            val response = caller.execute()
            if (response.isSuccessful) {
                return response.body().orEmpty()
            } else {
                Timber.e("Index request error: " + response.message())
            }
        } catch (e: IOException) {
            Timber.e("Index request error: " + e.message)
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
                cardRepository.syncAndSave(response.body()!!)
                Timber.i("Updated: " + card.getData())
            } else {
                Timber.e("Save request error: " + response.message() + " uuid: " + card.uuid)
            }
        } catch (e: IOException) {
            Timber.e("Save request error: " + e.message + " uuid: " + card.uuid)
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
}
