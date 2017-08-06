package br.com.jonathanzanella.myexpenses.helpers

import java.text.NumberFormat

object CurrencyHelper {
    private val TOTAL_CENTS = 100.0

    fun format(cents: Int): String {
        return NumberFormat.getCurrencyInstance().format(cents / TOTAL_CENTS)
    }

    internal fun format(cents: Double): String {
        return NumberFormat.getCurrencyInstance().format(cents / TOTAL_CENTS)
    }
}
