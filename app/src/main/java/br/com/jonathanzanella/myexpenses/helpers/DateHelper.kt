package br.com.jonathanzanella.myexpenses.helpers

import org.joda.time.DateTime

object DateHelper {
    private val MAX_HOURS_OF_DAY = 23
    private val MAX_MINUTES_OF_HOUR = 59
    private val MAX_SECONDS_OF_MINUTE = 59
    private val MAX_MILLISECONDS_OF_SECOND = 999

    @JvmStatic fun firstDayOfMonth(date: DateTime): DateTime {
        return firstMillisOfDay(date.dayOfMonth().withMinimumValue())
    }

    @JvmStatic fun firstMillisOfDay(date: DateTime): DateTime {
        return date.withMillisOfDay(0)
    }

    @JvmStatic fun lastDayOfMonth(date: DateTime): DateTime {
        return lastMillisOfDay(date.dayOfMonth().withMaximumValue())
    }

    @JvmStatic fun lastMillisOfDay(date: DateTime): DateTime {
        return date.withTime(MAX_HOURS_OF_DAY, MAX_MINUTES_OF_HOUR, MAX_SECONDS_OF_MINUTE, MAX_MILLISECONDS_OF_SECOND)
    }
}