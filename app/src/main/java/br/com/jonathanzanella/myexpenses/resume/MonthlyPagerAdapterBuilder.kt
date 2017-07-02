package br.com.jonathanzanella.myexpenses.resume

import android.content.Context
import br.com.jonathanzanella.myexpenses.views.BaseView
import org.joda.time.DateTime

interface MonthlyPagerAdapterBuilder {
    fun buildView(ctx: Context, date: DateTime): BaseView
}
