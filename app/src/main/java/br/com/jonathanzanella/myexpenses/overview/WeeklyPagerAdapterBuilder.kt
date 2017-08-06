package br.com.jonathanzanella.myexpenses.overview

import android.content.Context
import android.view.View

interface WeeklyPagerAdapterBuilder {
    fun buildView(ctx: Context, period: WeeklyPagerAdapter.Period): View
}
