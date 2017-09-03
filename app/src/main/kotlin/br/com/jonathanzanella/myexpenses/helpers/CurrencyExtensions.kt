package br.com.jonathanzanella.myexpenses.helpers

import java.text.NumberFormat

const val TOTAL_CENTS = 100.0

fun Int.toCurrencyFormatted(): String = NumberFormat.getCurrencyInstance().format(this / TOTAL_CENTS)
fun Double.toCurrencyFormatted(): String = NumberFormat.getCurrencyInstance().format(this / TOTAL_CENTS)
