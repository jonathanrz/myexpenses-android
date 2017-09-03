package br.com.jonathanzanella.myexpenses.database

import android.arch.persistence.room.TypeConverter
import br.com.jonathanzanella.myexpenses.card.CardType
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import org.joda.time.DateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?) = value?.let { DateTime(it) }

    @TypeConverter
    fun dateToTimestamp(date: DateTime?) = date?.millis

    @TypeConverter
    fun toCardType(type: String) =
        when (type) {
            CardType.CREDIT.value -> CardType.CREDIT
            CardType.DEBIT.value -> CardType.DEBIT
            else -> throw IllegalArgumentException("Could not recognize card type")
        }

    @TypeConverter
    fun fromCardType(type: CardType) =
        when(type) {
            CardType.CREDIT -> CardType.CREDIT.value
            CardType.DEBIT -> CardType.DEBIT.value
        }

    @TypeConverter
    fun toChargeableType(type: String) =
            when (type) {
                ChargeableType.ACCOUNT.name -> ChargeableType.ACCOUNT
                ChargeableType.DEBIT_CARD.name -> ChargeableType.DEBIT_CARD
                ChargeableType.CREDIT_CARD.name -> ChargeableType.CREDIT_CARD
                else -> throw IllegalArgumentException("Could not recognize card type")
            }

    @TypeConverter
    fun fromChargeableType(type: ChargeableType) =
            when(type) {
                ChargeableType.ACCOUNT -> ChargeableType.ACCOUNT.name
                ChargeableType.DEBIT_CARD -> ChargeableType.DEBIT_CARD.name
                ChargeableType.CREDIT_CARD -> ChargeableType.CREDIT_CARD.name
            }
}
