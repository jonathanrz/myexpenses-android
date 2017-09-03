package br.com.jonathanzanella.myexpenses.resume

import android.content.Context
import android.view.View
import org.joda.time.DateTime

interface MonthlyPagerAdapterBuilder {
    fun buildView(ctx: Context, date: DateTime): View
}
