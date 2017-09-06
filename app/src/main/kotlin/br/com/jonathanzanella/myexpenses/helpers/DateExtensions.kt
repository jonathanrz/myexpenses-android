package br.com.jonathanzanella.myexpenses.helpers

import org.joda.time.DateTime

const val MAX_HOURS_OF_DAY = 23
const val MAX_MINUTES_OF_HOUR = 59
const val MAX_SECONDS_OF_MINUTE = 59
const val MAX_MILLISECONDS_OF_SECOND = 999

fun DateTime.firstDayOfMonth(): DateTime = this.dayOfMonth().withMinimumValue().firstMillisOfDay()
fun DateTime.firstMillisOfDay(): DateTime = this.withTime(0, 0, 0, 0)
fun DateTime.lastDayOfMonth(): DateTime = this.dayOfMonth().withMaximumValue().lastMillisOfDay()
fun DateTime.lastMillisOfDay(): DateTime = this.withTime(MAX_HOURS_OF_DAY, MAX_MINUTES_OF_HOUR, MAX_SECONDS_OF_MINUTE, MAX_MILLISECONDS_OF_SECOND)
