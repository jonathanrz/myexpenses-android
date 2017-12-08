package br.com.jonathanzanella.myexpenses.transaction

import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

interface Transaction {
    var uuid: String?
    val name: String?
    val amount: Int
    fun credited(): Boolean
    fun debited(): Boolean
    fun getDate(): DateTime

    companion object {
        val SIMPLE_DATE_FORMAT = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    }
}
