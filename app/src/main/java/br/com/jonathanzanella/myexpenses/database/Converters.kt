package br.com.jonathanzanella.myexpenses.database

import android.arch.persistence.room.TypeConverter
import br.com.jonathanzanella.myexpenses.chargeable.ChargeableType
import org.joda.time.DateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): DateTime? {
        return value?.let { DateTime(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: DateTime?): Long? {
        return date?.millis
    }

    @TypeConverter
    fun toChargeableType(type: String): ChargeableType {
        return when (type) {
            "ACCOUNT" -> ChargeableType.ACCOUNT
            "DEBIT_CARD" -> ChargeableType.DEBIT_CARD
            "CREDIT_CARD" -> ChargeableType.CREDIT_CARD
            else -> throw IllegalArgumentException("Could not recognize chargeable type")
        }
    }

    @TypeConverter
    fun fromChargeableType(type: ChargeableType) =
        when(type) {
            ChargeableType.ACCOUNT -> "ACCOUNT"
            ChargeableType.DEBIT_CARD -> "DEBIT_CARD"
            ChargeableType.CREDIT_CARD -> "CREDIT_CARD"
        }
}